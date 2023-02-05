package in.wynk.client.config;

import in.wynk.client.aspect.ClientAwareAspect;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientAspectConfig {

    @Bean
    public ClientAwareAspect clientAwareAspect() {
        return Aspects.aspectOf(ClientAwareAspect.class);
    }

}
