package in.wynk.auth.event;

import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

public class UserDetailsAuthenticationSuccessEvent extends InteractiveAuthenticationSuccessEvent {

    public UserDetailsAuthenticationSuccessEvent(Authentication authentication, Class<?> generatedBy) {
        super(authentication, generatedBy);
    }

}
