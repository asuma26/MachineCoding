package in.wynk.http.interceptor;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static in.wynk.http.request.CachedBodyHttpServletRequest.WYNK_REQUEST_ID;
import static in.wynk.logging.constants.LoggingConstants.REQUEST_ID;

@Slf4j
@NoArgsConstructor
public class CommonHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (StringUtils.isNotBlank(MDC.get(REQUEST_ID))) {
            request.getHeaders().add(WYNK_REQUEST_ID, MDC.get(REQUEST_ID));
        }
        try {
            logRequest(request, body);
        } catch (Exception e) {
            log.error("Unable to log request: {}", request);
        }
        ClientHttpResponse response = execution.execute(request, body);
        try {
            logResponse(response);
        } catch (Exception e) {
            log.error("Unable to log response for request: {}", request);
        }
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            log.debug("\n===========================request begin================================================"
                            + "\nURI         : {}"
                            + "\nMethod      : {}"
                            + "\nHeaders     : {}"
                            + "\nRequest body: {}"
                            + "\n==========================request end================================================\n",
                    request.getURI(), request.getMethod().name(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));
        } else {
            log.info("{}: {} Headers:{}, Body: {}", request.getMethod().name(), request.getURI(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("\n============================response begin=========================================="
                            + "\nStatus code  : {}"
                            + "\nHeaders      : {}"
                            + "\nResponse body: {}"
                            + "\n=======================response end=================================================\n",
                    response.getStatusCode(), response.getHeaders(), StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        } else {
            log.info("Response: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        }
    }

}
