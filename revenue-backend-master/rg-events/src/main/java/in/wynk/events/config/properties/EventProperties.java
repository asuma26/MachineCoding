package in.wynk.events.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("event")
@Getter
@Setter
public class EventProperties {

    private CorsRegistry cors;

    @Getter
    @Setter
    public static class CorsRegistry {

        private int maxAge;
        private Allowed allowed;

        @Getter
        @Setter
        public static class Allowed {

            private String[] origins;
            private String[] methods;

        }

    }

}
