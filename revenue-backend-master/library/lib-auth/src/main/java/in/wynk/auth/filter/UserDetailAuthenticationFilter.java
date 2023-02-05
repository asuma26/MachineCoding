package in.wynk.auth.filter;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.constant.BeanConstant;
import in.wynk.auth.dto.PreAuthUserDetailAuthenticationToken;
import in.wynk.auth.event.BearerTokenGenerationSuccessEvent;
import in.wynk.auth.event.UserDetailsAuthenticationSuccessEvent;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import in.wynk.auth.service.IJwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDetailAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailAuthenticationFilter.class);

    private final IJwtTokenService jwtTokenService;
    private final AbstractPreAuthTokenMapper preAuthTokenMapper;
    private final AuthenticationManager authenticationManager;

    public UserDetailAuthenticationFilter(IJwtTokenService jwtTokenService,
                                          AuthenticationManager authenticationManager,
                                          ApplicationEventPublisher eventPublisher,
                                          @Qualifier(BeanConstant.PRE_AUTH_USER_DETAILS_TOKEN_MAPPER) AbstractPreAuthTokenMapper preAuthTokenMapper) {
        super();
        this.jwtTokenService = jwtTokenService;
        this.preAuthTokenMapper = preAuthTokenMapper;
        this.authenticationManager = authenticationManager;
        super.setApplicationEventPublisher(eventPublisher);
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(AuthConstant.AUTH_ENDPOINT, AuthConstant.AUTH_HTTP_POST));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        logger.info("Attempting user details authentication");
        Authentication preAuthenticationUserDetails = this.preAuthTokenMapper.map(request);
        logger.debug("attempting authentication for principal {}", preAuthenticationUserDetails.getPrincipal());
        return this.authenticationManager.authenticate(preAuthenticationUserDetails);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        logger.debug("authentication successful for principal {}", authResult.getPrincipal());
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        SecurityContextHolder.getContext().setAuthentication(authResult);
        PreAuthUserDetailAuthenticationToken userDetails = (PreAuthUserDetailAuthenticationToken) authResult;
        if (userDetails.getHeaders() != null && userDetails.getHeaders().size() > 0)
            headers.putAll(userDetails.getHeaders());
        if (userDetails.getBody() != null && userDetails.getBody().size() > 0)
            body.putAll(userDetails.getBody());
        if (userDetails.getAuthorities() != null && userDetails.getAuthorities().size() > 0) {
            body.put(AuthConstant.GRANTED_AUTHORITIES, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }
        String token = jwtTokenService.create(userDetails.getPrincipal(), headers, body);
        logger.debug("jwt token is successfully generated for principal {}", authResult.getPrincipal());
        response.addHeader(AuthConstant.JWT_TOKEN_HEADER, AuthConstant.AUTH_TOKEN_PREFIX + token);
        response.setStatus(HttpServletResponse.SC_OK);

        if (this.eventPublisher != null) {
            logger.debug("authentication successful event published for principal {}", authResult.getPrincipal());
            eventPublisher.publishEvent(new BearerTokenGenerationSuccessEvent(userDetails.getPrincipal(), token, request.getHeader("x-wynk-did"), request.getHeader("x-client-id")));
            eventPublisher.publishEvent(new UserDetailsAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }

    }

}
