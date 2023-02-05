package in.wynk.subscription.auth.filter;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.filter.UserDetailAuthenticationFilter;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StaticWebAuthenticationFilter extends BasicAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailAuthenticationFilter.class);

    private final AbstractPreAuthTokenMapper preAuthTokenMapper;
    private final AuthenticationManager authenticationManager;

    public StaticWebAuthenticationFilter(AuthenticationManager authenticationManager, AbstractPreAuthTokenMapper preAuthTokenMapper) {
        super(authenticationManager);
        this.preAuthTokenMapper = preAuthTokenMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("in Static Web Filter");
        String bsyDigestHeader = request.getHeader(AuthConstant.X_BSY_MG);
        if (StringUtils.isAllBlank(bsyDigestHeader)) {
            chain.doFilter(request, response);
            return;
        }
        logger.info("attempting Static Web Auth");
        Authentication preAuthenticationClientServerDetails = this.preAuthTokenMapper.map(request);
        logger.debug("attempting authentication for principal-digest {}", preAuthenticationClientServerDetails.getPrincipal());
        Authentication authentication = this.authenticationManager.authenticate(preAuthenticationClientServerDetails);
        if (authentication.isAuthenticated()) {
            onSuccessfulAuthentication(request, response, authentication);
        }
        chain.doFilter(request, response);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        logger.debug("authentication successful for principal-digest {}", authResult.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authResult);
        logger.debug("Atkn header token authorization is successful {}", authResult.getPrincipal());
    }
}
