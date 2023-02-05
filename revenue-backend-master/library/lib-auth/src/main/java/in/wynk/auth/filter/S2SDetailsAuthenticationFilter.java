package in.wynk.auth.filter;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.constant.BeanConstant;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class S2SDetailsAuthenticationFilter extends BasicAuthenticationFilter implements ApplicationEventPublisherAware {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailAuthenticationFilter.class);

    private final AbstractPreAuthTokenMapper preAuthTokenMapper;
    private final AuthenticationManager authenticationManager;

    public S2SDetailsAuthenticationFilter(AuthenticationManager authenticationManager, @Qualifier(BeanConstant.PRE_AUTH_S2S_DETAILS_TOKEN_MAPPER) AbstractPreAuthTokenMapper preAuthTokenMapper) {
        super(authenticationManager);
        this.preAuthTokenMapper = preAuthTokenMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("inside s2s authentication");
        String bsyAtknHeader = request.getHeader(AuthConstant.X_BSY_ATKN);
        String wynkAtknHeader = request.getHeader(AuthConstant.X_WYNK_ATKN);
        if (StringUtils.isAllBlank(bsyAtknHeader, wynkAtknHeader)) {
            chain.doFilter(request, response);
            return;
        }
        logger.info("attemping s2s authentication");
        Authentication preAuthenticationS2SDetails = this.preAuthTokenMapper.map(request);
        logger.debug("attempting authentication for principal {}", preAuthenticationS2SDetails.getPrincipal());
        Authentication authentication = this.authenticationManager.authenticate(preAuthenticationS2SDetails);
        if (authentication.isAuthenticated()) {
            onSuccessfulAuthentication(request, response, authentication);
        }
        chain.doFilter(request, response);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        logger.debug("authentication successful for principal {}", authResult.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authResult);
        logger.debug("Atkn header token authorization is successful {}", authResult.getPrincipal());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {

    }
}
