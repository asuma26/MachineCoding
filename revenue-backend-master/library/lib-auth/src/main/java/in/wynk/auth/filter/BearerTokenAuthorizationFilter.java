package in.wynk.auth.filter;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.constant.BeanConstant;
import in.wynk.auth.event.BearerTokenAuthenticationSuccessEvent;
import in.wynk.auth.event.BearerTokenGenerationUnsuccessfulEvent;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BearerTokenAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(BearerTokenAuthorizationFilter.class);

    private ApplicationEventPublisher eventPublisher;
    private final AbstractPreAuthTokenMapper preAuthTokenMapper;
    private final AuthenticationManager authenticationManager;

    public BearerTokenAuthorizationFilter(AuthenticationManager authenticationManager,
                                          ApplicationEventPublisher eventPublisher,
                                          @Qualifier(BeanConstant.PRE_AUTH_JWT_DETAILS_TOKEN_MAPPER) AbstractPreAuthTokenMapper preAuthTokenMapper) {
        super(authenticationManager);
        this.preAuthTokenMapper = preAuthTokenMapper;
        this.eventPublisher = eventPublisher;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("inside BearerToken authentication");
        String bearer = request.getHeader(AuthConstant.JWT_TOKEN_HEADER);
        if (isUserAuthenticationRequired(bearer)) {
            chain.doFilter(request, response);
            return;
        }
        logger.info("attempting BearerToken Auth");
        Authentication preAuthenticationToken = this.preAuthTokenMapper.map(request);
        logger.debug("attempting authorization for bearer token {}", preAuthenticationToken.getPrincipal());
        Authentication authentication = this.attemptAuthentication(preAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()) {
            onSuccessfulAuthentication(request, response, authentication);
        } else {
            onUnsuccessfulAuthentication(request, response, authentication);
        }
        chain.doFilter(request, response);
    }

    public Authentication attemptAuthentication(Authentication authentication) throws AuthenticationException {
        return this.authenticationManager.authenticate(authentication);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        logger.debug("Bearer token authorization is successful {}", authResult.getPrincipal());
        if (this.eventPublisher != null) {
            logger.debug("Bearer token authorization event is published {}", authResult.getPrincipal());
            eventPublisher.publishEvent(new BearerTokenAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }
    }


    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        logger.debug("Bearer token authorization is unsuccessful {}", authResult.getPrincipal());
        if (this.eventPublisher != null) {
            logger.debug("Bearer token authorization event is published {}", authResult.getPrincipal());
            eventPublisher.publishEvent(new BearerTokenGenerationUnsuccessfulEvent((String) authResult.getPrincipal(), request.getHeader("x-wynk-did"),
                    request.getHeader("x-client-id")));
        }
    }

    private boolean isUserAuthenticationRequired(String bearer) {
        return bearer == null || !bearer.startsWith(AuthConstant.AUTH_TOKEN_PREFIX);
    }
}
