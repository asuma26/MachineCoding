package in.wynk.subscription.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.queue.constant.BeanConstant;
import in.wynk.queue.service.ISqsManagerService;
import in.wynk.subscription.consumer.SubscriptionProvisioningConsumerPollingQueue;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.extractor.SubscriptionProvisioningSQSMessageExtractor;
import in.wynk.subscription.service.IUserPlanManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class SubscriptionQueueConfig {

    @Bean
    public SubscriptionProvisioningConsumerPollingQueue paymentReconciliationConsumerPollingQueue(@Value("${subscription.pooling.queue.provisioning.name}") String queueName,
                                                                                                  @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient,
                                                                                                  ObjectMapper objectMapper,
                                                                                                  SubscriptionProvisioningSQSMessageExtractor subscriptionProvisioningSQSMessageExtractor,
                                                                                                  IUserPlanManager userPlanManager,
                                                                                                  ISqsManagerService sqsManagerService,
                                                                                                  SubscriptionCachingService subscriptionCachingService) {
        return new SubscriptionProvisioningConsumerPollingQueue(queueName,
                sqsClient,
                objectMapper,
                userPlanManager,
                subscriptionProvisioningSQSMessageExtractor,
                sqsManagerService,
                threadPoolExecutor(),
                scheduledThreadPoolExecutor()
        );
    }

    @Bean
    public SubscriptionProvisioningSQSMessageExtractor subscriptionProvisioningSQSMessageExtractor(@Value("${subscription.pooling.queue.provisioning.name}") String queueName,
                                                                                                   @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient) {
        return new SubscriptionProvisioningSQSMessageExtractor(queueName, sqsClient);
    }

    private ExecutorService threadPoolExecutor() {
        return Executors.newFixedThreadPool(2);
    }

    private ScheduledExecutorService scheduledThreadPoolExecutor() {
        return Executors.newScheduledThreadPool(2);
    }


}
