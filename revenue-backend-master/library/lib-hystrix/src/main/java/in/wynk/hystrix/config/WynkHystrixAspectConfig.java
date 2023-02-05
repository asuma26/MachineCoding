package in.wynk.hystrix.config;

import in.wynk.hystrix.aspect.WynkHystrixAspect;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WynkHystrixAspectConfig {

    @Bean
    public WynkHystrixAspect wynkHystrixAspect() {
        return Aspects.aspectOf(WynkHystrixAspect.class);
    }

}
