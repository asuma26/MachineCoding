package in.wynk.auth.provider;

import in.wynk.auth.service.ITokenDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class BearerTokenAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(BearerTokenAuthenticationProvider.class);

    private final ITokenDetailsService tokenDetailsService;

    public BearerTokenAuthenticationProvider(ITokenDetailsService tokenDetailsService) {
        this.tokenDetailsService = tokenDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("authenticating bearer token");
        String token = (String) authentication.getPrincipal();
        return tokenDetailsService.loadTokenDetails(token);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        logger.debug("is authentication type supported {} ", PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication));
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
