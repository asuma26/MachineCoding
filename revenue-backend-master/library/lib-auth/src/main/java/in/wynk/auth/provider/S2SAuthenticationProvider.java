package in.wynk.auth.provider;

import in.wynk.auth.dto.S2SAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.service.IS2SDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class S2SAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(S2SAuthenticationProvider.class);

    private final IS2SDetailsService s2sDetailsService;

    public S2SAuthenticationProvider(IS2SDetailsService s2sDetailsService) {
        this.s2sDetailsService = s2sDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("authenticating s2s details");
        Authentication loadedAuthentication = s2sDetailsService.loadS2SDetails(authentication.getPrincipal());
        if (loadedAuthentication.getCredentials().equals(authentication.getCredentials())) {
            loadedAuthentication.setAuthenticated(true);
        } else {
            logger.info("Request Signature: {} does not match with computed signature: {}", authentication.getCredentials(), loadedAuthentication.getCredentials());
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH006);
        }
        return loadedAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        logger.debug("S2SAuthenticationToken authentication type supported ? {}", S2SAuthenticationToken.class.isAssignableFrom(authentication));
        return S2SAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
