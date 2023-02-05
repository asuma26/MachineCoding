package in.wynk.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.encoder.EncoderBase;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.IGNORE_UNKNOWN;

public class WynkErrorEncoder extends EncoderBase<ILoggingEvent> implements PreSerializationTransformer<ILoggingEvent> {

    private static final Logger logger = LoggerFactory.getLogger(WynkErrorEncoder.class);
    public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", TimeZone.getTimeZone("UTC"));
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(IGNORE_UNKNOWN, true);
    private static final StackTraceElement DEFAULT_CALLER_DATA = new StackTraceElement("", "", "", 0);
    private static final String[] NUMBER_TYPES = {":int", ":long", ":integer"};
    private static String HOST;
    private static String IP;

    public WynkErrorEncoder() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(WynkErrorEncoder::init);
    }

    private static void init() {
        String ip = "unknown";
        String host = "unknown";
        try {
            URLConnection connection = new URL("http://169.254.169.254/latest/meta-data/local-ipv4").openConnection();
            connection.setConnectTimeout(1000);
            Reader ipReader = new InputStreamReader(connection.getInputStream());
            BufferedReader ipBuffer = new BufferedReader(ipReader);
            host = InetAddress.getLocalHost().getCanonicalHostName();
            if (ipReader.ready()) {
                ip = ipBuffer.readLine();
            }
        } catch (IOException e) {
            logger.trace("An Error Occurred while fetching ip/host from aws ec3 meta, fallback to default docker ip/host", e);
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
                host = InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException unknownHostException) {
                host = "unknown";
                ip = "unknown";
                logger.trace(unknownHostException.getMessage(), e);
            }
        }
        HOST = host;
        IP = ip;
    }

    public static String getHost() {
        return HOST;
    }

    public static String getIp() {
        return IP;
    }

    private ObjectNode getObjectNode(ILoggingEvent event) {
        ObjectNode eventNode = MAPPER.createObjectNode();
        eventNode.put("@timestamp", ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS.format(event.getTimeStamp()));
        eventNode.put("@version", 1);
        eventNode.put("message", event.getFormattedMessage());
        eventNode.put("source_host", HOST);
        eventNode.put("source_ip", IP);
        createFields(eventNode, event);
        return eventNode;
    }

    @Override
    public Serializable transform(ILoggingEvent event) {
        try {
            return MAPPER.writeValueAsString(getObjectNode(event)) + CoreConstants.LINE_SEPARATOR;
        } catch (Exception e) {
            // eat it
            return null;
        }
    }

    private void createFields(ObjectNode fieldsNode, ILoggingEvent event) {
        fieldsNode.put("loggerName", event.getLoggerName());
        fieldsNode.put("threadName", event.getThreadName());
        fieldsNode.put("level", event.getLevel().toString());
        fieldsNode.put("levelValue", event.getLevel().toInt());
        Marker marker = BaseLoggingMarkers.APPLICATION_ERROR;
        if (event.getMarker() != null) {
            marker = event.getMarker();
        }
        fieldsNode.put("marker", marker.getName());
        StackTraceElement callerData = extractCallerData(event);
        fieldsNode.put("className", callerData.getClassName());
        fieldsNode.put("methodName", callerData.getMethodName());
        fieldsNode.put("fileName", callerData.getFileName());
        fieldsNode.put("lineNumber", callerData.getLineNumber());

        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy != null) {
            fieldsNode.put("stackTrace", ThrowableProxyUtil.asString(throwableProxy));
            while (null != throwableProxy.getCause()) {
                throwableProxy = throwableProxy.getCause();
            }
            fieldsNode.put("exceptionDetail", throwableProxy.getClassName() + ": " + throwableProxy.getMessage());
        }
        LoggerContextVO context = event.getLoggerContextVO();
        if (context != null) {
            addPropertiesAsFields(fieldsNode, context.getPropertyMap());
        }
        addPropertiesAsFields(fieldsNode, event.getMDCPropertyMap());

    }

    private void addPropertiesAsFields(ObjectNode fieldsNode, Map<String, String> properties) {
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.endsWithAny(key, NUMBER_TYPES)) {
                    try {
                        long longValue = Long.parseLong(value);
                        for (String type : NUMBER_TYPES) {
                            key = StringUtils.removeEndIgnoreCase(key, type);
                        }
                        fieldsNode.put(key, longValue);
                    } catch (NumberFormatException exception) {
                        for (String type : NUMBER_TYPES) {
                            key = StringUtils.removeEndIgnoreCase(key, type);
                        }
                        fieldsNode.put(key, value);
                    }
                } else {
                    fieldsNode.put(key, value);
                }

            }
        }
    }

    private StackTraceElement extractCallerData(ILoggingEvent event) {
        StackTraceElement[] ste = event.getCallerData();
        if ((ste == null) || (ste.length == 0)) {
            return DEFAULT_CALLER_DATA;
        }
        return ste[0];
    }

    @Override
    public byte[] headerBytes() {
        return CoreConstants.EMPTY_STRING.getBytes();
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        try {
            return (MAPPER.writeValueAsString(getObjectNode(event)) + CoreConstants.LINE_SEPARATOR).getBytes(Charset.defaultCharset());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json Processing Exception for event" + event.toString());
        }
    }

    @Override
    public byte[] footerBytes() {
        return CoreConstants.EMPTY_STRING.getBytes();
    }
}

