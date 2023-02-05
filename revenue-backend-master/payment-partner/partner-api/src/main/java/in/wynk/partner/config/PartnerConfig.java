package in.wynk.partner.config;

import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.service.IClientDetailsService;
import in.wynk.common.properties.CorsProperties;
import in.wynk.partner.security.provider.BasicPartnerAuthenticationProvider;
import in.wynk.partner.security.service.PartnerDetailsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties({CorsProperties.class})
public class PartnerConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    public PartnerConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowed().getOrigins())
                .allowedMethods(corsProperties.getAllowed().getMethods())
                .maxAge(corsProperties.getMaxAge());
    }

    @Profile("!local")
    @Bean(name = "applicationEventMultiCaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMultiCaster = new SimpleApplicationEventMulticaster();
        eventMultiCaster.setTaskExecutor(new SimpleAsyncTaskExecutor("partner-event-caster"));
        return eventMultiCaster;
    }

    @Bean
    public AuthenticationProvider basicPartnerAuthenticationProvider(IClientDetailsService<Client> clientDetailsService) {
        return new BasicPartnerAuthenticationProvider(basicPartnerDetailsService(clientDetailsService));
    }

    private PartnerDetailsService basicPartnerDetailsService(IClientDetailsService<Client> clientDetailsService) {
        return new PartnerDetailsService(clientDetailsService);
    }

}
