package in.wynk.order.config;

import com.netflix.hystrix.strategy.HystrixPlugins;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.client.service.ClientDetailsCachingService;
import in.wynk.common.context.WynkApplicationContext;
import in.wynk.order.hook.WynkOrderContextCopyCommandExecutionHook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class OrderConfig {

    @Value("${spring.application.name}")
    private String applicationAlias;

    @PostConstruct
    public void setupWynkOrderContextCopyHook() {
        HystrixPlugins.getInstance().registerCommandExecutionHook(new WynkOrderContextCopyCommandExecutionHook());
    }

    @Bean
    public WynkApplicationContext myApplicationContext(ClientDetailsCachingService cachingService) {
        ClientDetails client = (ClientDetails) cachingService.getClientByAlias(applicationAlias);
        return WynkApplicationContext.builder()
                .meta(client.getMeta())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientAlias(client.getAlias())
                .build();
    }

}
