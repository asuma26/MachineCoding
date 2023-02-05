package in.wynk.http.filter;

import in.wynk.http.request.CachedBodyHttpServletRequest;
import in.wynk.http.request.WynkRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static in.wynk.exception.constants.ExceptionConstants.REQUEST_ID;

public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        WynkRequestContext.setRequest(cachedRequest);
        MDC.put(REQUEST_ID, cachedRequest.getRequestId());
        logger.info("Logging Request [{}:{}, QueryParams:{}, Headers:{}, Body: {} ]", request.getMethod(), request.getRequestURL(), request.getQueryString(), cachedRequest.getCachedHeaders(), cachedRequest.getCachedBody());
        filterChain.doFilter(cachedRequest, response);
        logger.info("Logging Response Status: {}, Location: {}", response.getStatus(), response.getHeader(HttpHeaders.LOCATION));
        MDC.clear();
    }
}
