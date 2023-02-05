package in.wynk.order.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.order.consumer.OrderFulfilmentConsumerPollingQueue;
import in.wynk.order.consumer.OrderNotificationConsumerPollingQueue;
import in.wynk.order.consumer.OrderPlacementConsumerPollingQueue;
import in.wynk.order.extractor.OrderFulfilmentSQSMessageExtractor;
import in.wynk.order.extractor.OrderNotificationSQSMessageExtractor;
import in.wynk.order.extractor.OrderPlacementSQSMessageExtractor;
import in.wynk.queue.constant.BeanConstant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class OrderQueueConfig {

    @Bean
    public OrderPlacementConsumerPollingQueue orderPlacementConsumerPollingQueue(@Value("${order.pooling.queue.placement.name}") String queueName,
                                                                                 @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient,
                                                                                 ObjectMapper objectMapper,
                                                                                 OrderPlacementSQSMessageExtractor orderPlacementSQSMessageExtractor) {
        return new OrderPlacementConsumerPollingQueue(queueName,
                sqsClient,
                objectMapper,
                orderPlacementSQSMessageExtractor,
                threadPoolExecutor(),
                scheduledThreadPoolExecutor());
    }


    @Bean
    public OrderPlacementSQSMessageExtractor orderPlacementSQSMessageExtractor(@Value("${order.pooling.queue.placement.name}") String queueName,
                                                                               @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient) {
        return new OrderPlacementSQSMessageExtractor(queueName, sqsClient);
    }

    @Bean
    public OrderFulfilmentConsumerPollingQueue orderFulfilmentConsumerPollingQueue(@Value("${order.pooling.queue.fulfilment.name}") String queueName,
                                                                                   @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient,
                                                                                   ObjectMapper objectMapper,
                                                                                   OrderFulfilmentSQSMessageExtractor orderFulfilmentSQSMessageExtractor) {
        return new OrderFulfilmentConsumerPollingQueue(queueName,
                sqsClient,
                objectMapper,
                orderFulfilmentSQSMessageExtractor,
                threadPoolExecutor(),
                scheduledThreadPoolExecutor());
    }


    @Bean
    public OrderFulfilmentSQSMessageExtractor orderFulfilmentSQSMessageExtractor(@Value("${order.pooling.queue.fulfilment.name}") String queueName,
                                                                                 @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient) {
        return new OrderFulfilmentSQSMessageExtractor(queueName, sqsClient);
    }


    @Bean
    public OrderNotificationConsumerPollingQueue orderNotificationConsumerPollingQueue(@Value("${order.pooling.queue.notification.name}") String queueName,
                                                                                       @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient,
                                                                                       ObjectMapper objectMapper,
                                                                                       OrderNotificationSQSMessageExtractor orderNotificationSQSMessageExtractor) {
        return new OrderNotificationConsumerPollingQueue(queueName,
                sqsClient,
                objectMapper,
                orderNotificationSQSMessageExtractor,
                threadPoolExecutor(),
                scheduledThreadPoolExecutor());
    }


    @Bean
    public OrderNotificationSQSMessageExtractor orderNotificationSQSMessageExtractor(@Value("${order.pooling.queue.notification.name}") String queueName,
                                                                                     @Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqsClient) {
        return new OrderNotificationSQSMessageExtractor(queueName, sqsClient);
    }


    private ExecutorService threadPoolExecutor() {
        return Executors.newFixedThreadPool(2);
    }

    private ScheduledExecutorService scheduledThreadPoolExecutor() {
        return Executors.newScheduledThreadPool(2);
    }

}
