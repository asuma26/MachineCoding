package in.wynk.auth.config;

import in.wynk.auth.config.properties.SecurityProperties;
import in.wynk.auth.entrypoint.AuthenticationFailureEntryPoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@ConditionalOnProperty("wynk.security.default")
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class AuthConfig {

    @Bean
    public AuthenticationFailureEntryPoint authenticationFailureEntryPoint() {
        return new AuthenticationFailureEntryPoint();
    }

}
