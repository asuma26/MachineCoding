package in.wynk.subscription.auth.provider;

import in.wynk.auth.dto.PreAuthClientDetailAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.subscription.auth.IStaticWebClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class StaticWebViewAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(StaticWebViewAuthenticationProvider.class);

    private final IStaticWebClientService staticWebClientService;

    public StaticWebViewAuthenticationProvider(IStaticWebClientService staticWebClientService) {
        this.staticWebClientService = staticWebClientService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("authenticating client to server details");
        Authentication loadedAuthentication = staticWebClientService.loadClientSignature();
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
        logger.debug("PreAuthClientDetailAuthenticationToken authentication type supported ? {}", PreAuthClientDetailAuthenticationToken.class.isAssignableFrom(authentication));
        return PreAuthClientDetailAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
