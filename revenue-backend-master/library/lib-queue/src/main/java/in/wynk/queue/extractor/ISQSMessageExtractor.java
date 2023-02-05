package in.wynk.queue.extractor;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;

public interface ISQSMessageExtractor {

    List<Message> extract();

}
