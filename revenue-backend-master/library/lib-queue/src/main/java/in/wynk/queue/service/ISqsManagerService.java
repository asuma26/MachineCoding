package in.wynk.queue.service;

import com.amazonaws.services.sqs.model.MessageAttributeValue;

import java.util.Map;

public interface ISqsManagerService {

     <T> void publishSQSMessage(T message);

     <T> void publishSQSMessage(T message, Map<String, MessageAttributeValue> messageAttributes);

}
