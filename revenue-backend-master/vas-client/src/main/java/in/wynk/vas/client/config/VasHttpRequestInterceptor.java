package in.wynk.vas.client.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Abhishek
 * @created 07/07/20
 */
@Slf4j
public class VasHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        try {
            logRequest(request, body);
            logResponse(response);
        } catch (Exception e) {
            log.error("Unable to log request response for request: {}", request);
        }
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            log.debug("\n===========================request begin================================================"
                            + "\nURI         : {}"
                            + "\nHeaders     : {}"
                            + "\nRequest body: {}"
                            + "\n==========================request end================================================\n",
                    request.getURI(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));
        } else {
            log.info("\nURI: {}\nRequest body: {}\n", request.getURI(), new String(body, StandardCharsets.UTF_8));
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