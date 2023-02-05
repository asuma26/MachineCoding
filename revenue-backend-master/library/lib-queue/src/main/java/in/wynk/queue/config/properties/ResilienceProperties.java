package in.wynk.queue.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("resilience")
public class ResilienceProperties {

    private RetryProperties retryProperties;

    @Getter
    @Setter
    public static class RetryProperties {

        private int maxAttempts;
        private long waitDuration;

    }

}
