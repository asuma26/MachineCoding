package in.wynk.thanks.publisher;

import com.google.gson.Gson;
import in.wynk.thanks.kinesis.KinesisPublisher;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ThanksKinesisPublisher<T> {
    @Autowired
    private KinesisPublisher kinesisPublisher;

    @Autowired
    private Gson gson;

    public abstract String getStreamName();

    public void publishEventToKinesis(String si, T event) {
        kinesisPublisher.publish(getStreamName(), si, gson.toJson(event));
    }
}
