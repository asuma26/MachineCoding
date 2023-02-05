package in.wynk.auth.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BearerTokenGenerationSuccessEvent extends ApplicationEvent {

    private final String token;
    private final String deviceId;
    private final String principal;
    private final String clientId;

    public BearerTokenGenerationSuccessEvent(String principal, String token, String deviceId, String clientId) {
        super(token);
        this.token = token;
        this.principal = principal;
        this.deviceId = deviceId;
        this.clientId = clientId;
    }
}
