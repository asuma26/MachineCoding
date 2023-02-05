package in.wynk.auth.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BearerTokenGenerationUnsuccessfulEvent extends ApplicationEvent {

    private final String deviceId;
    private final String clientId;
    private final String principal;

    public BearerTokenGenerationUnsuccessfulEvent(String principal, String deviceId, String clientId) {
        super(BearerTokenGenerationUnsuccessfulEvent.class.getCanonicalName());
        this.principal = principal;
        this.deviceId = deviceId;
        this.clientId = clientId;
    }
}
