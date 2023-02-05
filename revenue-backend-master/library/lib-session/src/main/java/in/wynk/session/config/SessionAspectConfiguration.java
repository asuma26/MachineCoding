package in.wynk.session.config;

import in.wynk.session.aspect.ManageSessionAspect;
import in.wynk.session.config.properties.SessionManagerProperties;
import org.aspectj.lang.Aspects;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ SessionManagerProperties.class })
public class SessionAspectConfiguration {

    @Bean
    public ManageSessionAspect manageSessionAspect() {
        return Aspects.aspectOf(ManageSessionAspect.class);
    }


}
