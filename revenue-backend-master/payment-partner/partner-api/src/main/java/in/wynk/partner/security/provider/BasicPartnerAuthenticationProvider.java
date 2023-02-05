package in.wynk.partner.security.provider;

import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.service.IBasicAuthDetailsService;
import in.wynk.partner.security.token.BasicPartnerAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Slf4j
@RequiredArgsConstructor
public class BasicPartnerAuthenticationProvider implements AuthenticationProvider {

    private final IBasicAuthDetailsService basicPartnerDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("authenticating basic partner details");
        Authentication loadedAuthentication = basicPartnerDetailsService.loadBasicAuthDetails(authentication.getPrincipal());
        if (loadedAuthentication.getCredentials().equals(authentication.getCredentials())) {
            loadedAuthentication.setAuthenticated(true);
        } else {
            log.info("Credentials are not matched against partner id: {}", authentication.getPrincipal());
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH006);
        }
        return loadedAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.debug("is authentication type supported ? {}", BasicPartnerAuthenticationToken.class.isAssignableFrom(authentication));
        return BasicPartnerAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
