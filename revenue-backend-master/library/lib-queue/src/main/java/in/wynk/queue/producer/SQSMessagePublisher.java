package in.wynk.queue.producer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageResult;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.queue.constant.BeanConstant;
import in.wynk.queue.constant.QueueConstant;
import in.wynk.queue.constant.QueueErrorType;
import in.wynk.queue.constant.QueueLoggingMarker;
import in.wynk.queue.dto.SendSQSMessageRequest;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service(BeanConstant.SQS_EVENT_PRODUCER)
public class SQSMessagePublisher implements ISQSMessagePublisher {

    private final AmazonSQS sqs;
    private final RetryRegistry retryRegistry;

    public SQSMessagePublisher(@Qualifier(BeanConstant.SQS_MANAGER) AmazonSQS sqs,
                               @Qualifier(BeanConstant.RESILIENCE_RETRY) RetryRegistry retryRegistry) {
        this.sqs = sqs;
        this.retryRegistry = retryRegistry;
    }

    @Override
    public String publish(SendSQSMessageRequest request) throws Exception {
        Retry retry = retryRegistry.retry(QueueConstant.MESSAGE_PUBLISHER_RETRY_KEY);
        return retry.executeCallable(() -> doPublish(request));
    }

    private String doPublish(SendSQSMessageRequest request) {

        this.preProcessing(request);
        SendMessageResult result;

        try {
            result = sqs.sendMessage(request);
        } catch (Exception e) {
            log.error(QueueLoggingMarker.SQS_ERROR, e.getMessage(), e);
            throw new WynkRuntimeException(QueueErrorType.SQS001, e);
        }

        this.postProcessing(result);

        return result.getMessageId();
    }

    private void preProcessing(SendSQSMessageRequest request) {
        String queueUrl = sqs.getQueueUrl(request.getQueueName()).getQueueUrl();
        request.setQueueUrl(queueUrl);
    }

    private void postProcessing(SendMessageResult result) {
        if (!StringUtils.isNotBlank(result.getMessageId()) || result.getSdkHttpMetadata().getHttpStatusCode() != HttpStatus.SC_OK) {
            throw new WynkRuntimeException(QueueErrorType.SQS002);
        }
    }

}
