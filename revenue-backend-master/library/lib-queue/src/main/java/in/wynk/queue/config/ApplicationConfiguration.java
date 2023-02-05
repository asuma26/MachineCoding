package in.wynk.queue.config;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.queue.config.properties.ResilienceProperties;
import in.wynk.queue.constant.BeanConstant;
import in.wynk.queue.manager.SQSPollingQueuesLifeCycleManager;
import in.wynk.queue.registry.IPollingQueueRegistryManager;
import in.wynk.queue.registry.SQSMessagePollingQueueRegistryManager;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.internal.InMemoryRetryRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties({ResilienceProperties.class})
public class ApplicationConfiguration {

    private final ResilienceProperties resilienceProperties;

    public ApplicationConfiguration(ResilienceProperties resilienceProperties) {
        this.resilienceProperties = resilienceProperties;
    }

    @Bean(BeanConstant.RESILIENCE_RETRY)
    public RetryRegistry retryRegistry() {
        return new InMemoryRetryRegistry(
                RetryConfig.custom()
                        .retryOnException(e -> e instanceof WynkRuntimeException)
                        .maxAttempts(resilienceProperties.getRetryProperties().getMaxAttempts())
                        .waitDuration(Duration.ofMillis(resilienceProperties.getRetryProperties().getWaitDuration()))
                        .build()
        );
    }

    @Bean(BeanConstant.SQS_POLLING_QUEUE_REGISTRY_MANAGER)
    public IPollingQueueRegistryManager sqsMessagePollingQueueRegistryManager() {
        return new SQSMessagePollingQueueRegistryManager();
    }

    @Bean
    public SQSPollingQueuesLifeCycleManager sqsPollingQueuesLifeCycleManager(@Qualifier(BeanConstant.SQS_POLLING_QUEUE_REGISTRY_MANAGER) IPollingQueueRegistryManager sqsMessagePollingQueueRegistryManager) {
        return new SQSPollingQueuesLifeCycleManager(sqsMessagePollingQueueRegistryManager);
    }

}
