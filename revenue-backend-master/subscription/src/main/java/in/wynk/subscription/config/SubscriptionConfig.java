package in.wynk.subscription.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import in.wynk.auth.constant.BeanConstant;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.client.service.ClientDetailsCachingService;
import in.wynk.common.context.WynkApplicationContext;
import in.wynk.common.properties.CorsProperties;
import in.wynk.queue.config.properties.AmazonSdkProperties;
import in.wynk.subscription.auth.IStaticWebClientService;
import in.wynk.subscription.auth.mapper.StaticWebviewAuthTokenMapper;
import in.wynk.subscription.auth.provider.StaticWebViewAuthenticationProvider;
import in.wynk.subscription.service.impl.StaticWebClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties({CorsProperties.class})
public class SubscriptionConfig implements WebMvcConfigurer {

    @Value("${spring.application.name}")
    private String applicationAlias;

    private final CorsProperties corsProperties;

    public SubscriptionConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowed().getOrigins())
                .allowedMethods(corsProperties.getAllowed().getMethods())
                .maxAge(corsProperties.getMaxAge());
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        return Executors.newWorkStealingPool();
    }

    @Profile("!local")
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor("sub-event"));
        return eventMulticaster;
    }

    @Bean
    public WynkApplicationContext myApplicationContext(ClientDetailsCachingService cachingService) {
        ClientDetails client = (ClientDetails) cachingService.getClientByAlias(applicationAlias);
        return WynkApplicationContext.builder()
                .meta(client.getMeta())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret()).clientAlias(client.getAlias())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public IStaticWebClientService clientToServerDetailsService() {
        return new StaticWebClientService();
    }

    @Bean(BeanConstant.PRE_AUTH_CLIENT_DETAILS_TOKEN_MAPPER)
    public StaticWebviewAuthTokenMapper preAuthClientToServerDetailsTokenMapper() {
        return new StaticWebviewAuthTokenMapper();
    }

    @Bean
    public AuthenticationProvider clientToServerAuthenticationProvider(IStaticWebClientService clientToServerDetailsService) {
        return new StaticWebViewAuthenticationProvider(clientToServerDetailsService);
    }

    @Bean
    public AmazonS3 amazonS3Client(AmazonSdkProperties sdkProperties) {
        return AmazonS3ClientBuilder.standard().withRegion(sdkProperties.getSdk().getRegions()).build();
    }
}
