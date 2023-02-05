package in.wynk.auth.provider;

import in.wynk.auth.dto.PreAuthUserDetailAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.service.IUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class UserDetailsAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsAuthenticationProvider.class);

    private final IUserDetailsService userDetailsService;

    public UserDetailsAuthenticationProvider(IUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("authenticating user details");
        Authentication loadedAuthentication = userDetailsService.loadUserDetails(authentication.getPrincipal());
        if (loadedAuthentication.getCredentials().equals(authentication.getCredentials())) {
            loadedAuthentication.setAuthenticated(true);
        } else {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH006);
        }
        return loadedAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        logger.debug("is authentication type supported ? ", PreAuthUserDetailAuthenticationToken.class.isAssignableFrom(authentication));
        return PreAuthUserDetailAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
