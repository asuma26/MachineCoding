package in.wynk.scheduler.config;

import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.client.service.ClientDetailsCachingService;
import in.wynk.common.context.WynkApplicationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class WynkSchedulerConfig {

    @Value("${spring.application.name}")
    private String applicationAlias;

    @Bean
    public WynkApplicationContext myApplicationContext(ClientDetailsCachingService cachingService) {
        ClientDetails client = (ClientDetails) cachingService.getClientByAlias(applicationAlias);
        return WynkApplicationContext.builder()
                .meta(client.getMeta())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .build();
    }
}
