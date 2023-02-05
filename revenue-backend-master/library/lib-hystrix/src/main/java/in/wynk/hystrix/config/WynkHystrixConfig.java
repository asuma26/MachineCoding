package in.wynk.hystrix.config;

import in.wynk.hystrix.config.properties.WynkHystrixProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({WynkHystrixProperty.class})
public class WynkHystrixConfig { }
