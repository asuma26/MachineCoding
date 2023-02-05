package in.wynk.http.request;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final String cachedBody;
    private Map<String, String[]> parameterMap;
    private HttpHeaders cachedHeaders;
    private final String requestId;

    public static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
    public static final String WYNK_REQUEST_ID = "x-wynk-rid";

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToString(requestInputStream, Charset.defaultCharset());
        this.cachedHeaders = headers(request);
        this.requestId = generateRequestId(request);
    }

    private HttpHeaders headers(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(Function.identity(), h -> Collections.list(request.getHeaders(h)),
                        (oldValue, newValue) -> newValue,
                        HttpHeaders::new
                ));
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody.getBytes());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // Create a reader from cachedContent and return it
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getParameter(String key) {
        Map<String, String[]> parameterMap = getParameterMap();
        String[] values = parameterMap.get(key);
        return values != null && values.length > 0 ? values[0] : null;
    }

    @Override
    public String[] getParameterValues(String key) {
        Map<String, String[]> parameterMap = getParameterMap();
        return parameterMap.get(key);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (parameterMap == null) {
            Map<String, String[]> result = new LinkedHashMap<>();
            decode(getQueryString(), result);
            decode(getCachedBody(), result);
            parameterMap = Collections.unmodifiableMap(result);
        }
        return parameterMap;
    }

    public String getCachedBody() {
        return cachedBody;
    }

    public HttpHeaders getCachedHeaders(){
        return cachedHeaders;
    }

    public static void toMap(Iterable<NameValuePair> inputParams, Map<String, String[]> toMap) {
        for (NameValuePair e : inputParams) {
            String key = e.getName();
            String value = e.getValue();
            if (toMap.containsKey(key)) {
                String[] newValue = ArrayUtils.addAll(toMap.get(key), value);
                toMap.remove(key);
                toMap.put(key, newValue);
            } else {
                toMap.put(key, new String[]{value});
            }
        }
    }

    private void decode(String queryString, Map<String, String[]> result) {
        if (queryString != null) toMap(decodeParams(queryString), result);
    }

    private Iterable<NameValuePair> decodeParams(String body) {
        List<NameValuePair> queryParams = URLEncodedUtils.parse(body, UTF8_CHARSET);
        try {
            if (isUrlEncodedFormPost()) {
                List<NameValuePair> postParams = URLEncodedUtils.parse(StreamUtils.copyToString(getInputStream(), UTF8_CHARSET), UTF8_CHARSET);
                queryParams.addAll(postParams);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return queryParams;
    }

    private boolean isUrlEncodedFormPost() {
        String contentType = getContentType();
        return (contentType != null && contentType.contains(APPLICATION_FORM_URLENCODED_VALUE) &&
                HttpMethod.POST.matches(getMethod()));
    }

    private String generateRequestId(HttpServletRequest request) {
        String wynkRequestId = request.getHeader(WYNK_REQUEST_ID);
        if(StringUtils.isNotBlank(wynkRequestId)){
            return wynkRequestId;
        }
        return String.format("%s-%s", ThreadLocalRandom.current().nextFloat(), System.currentTimeMillis());
    }

    public String getRequestId() {
        return requestId;
    }
}