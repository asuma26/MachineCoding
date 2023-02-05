package in.wynk.auth.event;

import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

public class BearerTokenAuthenticationSuccessEvent extends InteractiveAuthenticationSuccessEvent {

    public BearerTokenAuthenticationSuccessEvent(Authentication authentication, Class<?> generatedBy) {
        super(authentication, generatedBy);
    }
    
}
