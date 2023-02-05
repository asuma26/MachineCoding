package in.wynk.queue.producer;

import in.wynk.queue.dto.SendSQSMessageRequest;

public interface ISQSMessagePublisher {

     <T> String publish(SendSQSMessageRequest request) throws Exception;

}
