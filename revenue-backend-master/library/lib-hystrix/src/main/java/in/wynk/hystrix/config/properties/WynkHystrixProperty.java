package in.wynk.hystrix.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("hystrix")
public class WynkHystrixProperty {
    private boolean enabled;
}
