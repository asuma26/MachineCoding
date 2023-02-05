package in.wynk.queue.extractor;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.queue.constant.QueueErrorType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.util.List;

@Getter
@Slf4j
public abstract class AbstractSQSMessageExtractor implements ISQSMessageExtractor {

    private final AmazonSQS sqs;

    public AbstractSQSMessageExtractor(AmazonSQS sqs) {
        this.sqs = sqs;
    }

    @Override
    public List<Message> extract() {
        ReceiveMessageResult result;
        ReceiveMessageRequest request = this.buildReceiveMessageRequest();
        try {
            result = sqs.receiveMessage(request);
        } catch (Exception e) {
            throw new WynkRuntimeException(QueueErrorType.SQS003, e);
        }

        if (result.getSdkHttpMetadata().getHttpStatusCode() != HttpStatus.SC_OK) {
            throw new WynkRuntimeException(QueueErrorType.SQS004);
        }
        log.debug("received {} messages from SQS queue {}", result.getMessages().size(), request.getQueueUrl());

        return result.getMessages();
    }

    public abstract ReceiveMessageRequest buildReceiveMessageRequest();


}
