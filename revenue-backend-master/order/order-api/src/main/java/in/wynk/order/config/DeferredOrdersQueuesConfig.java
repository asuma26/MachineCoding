package in.wynk.order.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.order.consumer.DeferredOrdersConsumerPollingQueue;
import in.wynk.order.extractor.DeferredOrderSQSMessageExtractor;
import in.wynk.queue.constant.BeanConstant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class DeferredOrdersQueuesConfig {


    @Bean
    public DeferredOrdersConsumerPollingQueue deferredOrdersConsumerPollingQueue(@Value("${order.pooling.queue.deferred.name}") String queueName,
                                                                                 @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient,
                                                                                 ObjectMapper objectMapper,
                                                                                 DeferredOrderSQSMessageExtractor paymentRenewalSQSMessageExtractor) {
        return new DeferredOrdersConsumerPollingQueue(queueName,
                sqsClient,
                objectMapper,
                paymentRenewalSQSMessageExtractor,
                threadPoolExecutor(),
                scheduledThreadPoolExecutor());
    }

    @Bean
    public DeferredOrderSQSMessageExtractor deferredOrderSQSMessageExtractor(@Value("${order.pooling.queue.deferred.name}") String queueName,
                                                                             @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient) {
        return new DeferredOrderSQSMessageExtractor(queueName, sqsClient);
    }


    private ExecutorService threadPoolExecutor() {
        return Executors.newFixedThreadPool(2);
    }

    private ScheduledExecutorService scheduledThreadPoolExecutor() {
        return Executors.newScheduledThreadPool(2);
    }

}
