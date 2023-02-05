package in.wynk.utils.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.client.service.ClientDetailsCachingService;
import in.wynk.common.context.WynkApplicationContext;
import in.wynk.queue.config.properties.AmazonSdkProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class WcfSubscriptionConfig {

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

    @Bean
    public AmazonS3 amazonS3Client(AmazonSdkProperties sdkProperties) {
        return AmazonS3ClientBuilder.standard().withRegion(sdkProperties.getSdk().getRegions()).build();
    }
}
