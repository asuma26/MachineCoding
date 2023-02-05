package in.wynk.thanks.kinesis;

import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.google.common.util.concurrent.FutureCallback;
import in.wynk.thanks.logging.ThanksLoggingMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KinesisPublisherCallback implements FutureCallback<UserRecordResult> {

    private static final Logger logger = LoggerFactory.getLogger(KinesisPublisherCallback.class);
    private String stream;
    private String partitionKey;
    private String payload;

    public KinesisPublisherCallback(String stream, String partitionKey, String payload) {
        this.stream = stream;
        this.partitionKey = partitionKey;
        this.payload = payload;
    }

    @Override public void onSuccess(UserRecordResult result) {
        logger.debug(ThanksLoggingMarkers.KINESIS_PUBLISH_SUCCESS,"Success Push To Kinesis Callback, stream: {}, result, {}",stream, result.toString());
    }

    @Override public void onFailure(Throwable t) {
        logger.error(ThanksLoggingMarkers.KINESIS_PUBLISH_ERROR, "Kinesis Publish Error Callback, stream: {}, partitioinKey: {}, payload: {}",stream, partitionKey, payload, t);
    }
}
