package in.wynk.queue.dto;

import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.MessageSystemAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.queue.utils.QueueUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class SendSQSMessageRequest<T> extends SendMessageRequest {

    private String queueName;
    @Getter(AccessLevel.NONE)
    private String queueUrl;
    private T message;
    private Integer delaySeconds;
    private String messageDeduplicationId;
    private String messageGroupId;
    private Map<String, MessageAttributeValue> messageAttributes;
    private Map<String, MessageSystemAttributeValue> messageSystemAttributes;

    public Map<String, MessageAttributeValue> getMessageAttributes() {
        return messageAttributes != null ? messageAttributes : super.getMessageAttributes();
    }

    public Map<String, MessageSystemAttributeValue> getMessageSystemAttributes() {
        return this.messageSystemAttributes != null ? messageSystemAttributes : super.getMessageSystemAttributes();
    }

    public String getMessageBody() {
        try {
            return QueueUtils.mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new WynkRuntimeException(e);
        }
    }

}
