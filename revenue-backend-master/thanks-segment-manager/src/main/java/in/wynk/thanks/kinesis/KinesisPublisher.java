package in.wynk.thanks.kinesis;

import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import in.wynk.thanks.logging.ThanksLoggingMarkers;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service
public class KinesisPublisher {

    private static final Logger log = LoggerFactory.getLogger(KinesisPublisher.class);

    private static final String awsRegion = "ap-south-1";
    private       KinesisProducer kinesisProducer;

    @Value("${record.max.buffered.time:10000}")
    private long recordMaxBufferedTime = 2000L;
    @Value("${kinesis.max.connections:10}")
    private long maxConnections = 10L;
    @Value("${kinesis.request.timeout:2000}")
    private long requestTimeout = 2000L;
    @Value("${kinesis.publisher.pool.size:20}")
    private int publisherPoolSize = 20;

    @Value("${init.kpl:true}")
    private boolean initKpl = true;

    @PostConstruct
    private void init() {
        log.debug("initKpl: "+initKpl);
        if(initKpl) {
            KinesisProducerConfiguration config = new KinesisProducerConfiguration();
            config.setRegion(awsRegion);
            config.setRecordMaxBufferedTime(recordMaxBufferedTime);
            config.setMaxConnections(maxConnections);
            config.setRequestTimeout(requestTimeout);
            config.setThreadPoolSize(publisherPoolSize);
            config.setMetricsLevel("none");
            kinesisProducer = new KinesisProducer(config);
        }
    }

    public KinesisProducer getKinesisProducer() {
        return kinesisProducer;
    }

    public void publish(String stream, String partitionKey, String payload) {
        if(StringUtils.isEmpty(stream) || StringUtils.isEmpty(partitionKey) || StringUtils.isEmpty(payload)) {
            log.error(ThanksLoggingMarkers.KINESIS_PUBLISH_ERROR, String.format("Wrong data to publish to stream, stream name : %s, partitionKey : %s, payload : %s", stream, partitionKey, payload));
        }
        try {
            ByteBuffer data = ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8));
            ListenableFuture<UserRecordResult> future = kinesisProducer.addUserRecord(stream, partitionKey, data);
            Futures.addCallback(future, new KinesisPublisherCallback(stream, partitionKey, payload));
        } catch (Exception e) {
            log.error(ThanksLoggingMarkers.KINESIS_PUBLISH_ERROR, "Kinesis Publish Error, stream: {}, partitioinKey: {}, payload: {}",stream, partitionKey, payload, e);
        }
    }

//    public static void main(String[] args) throws Exception {
//        asyncTest();
//        System.out.println("DONE");
//    }
//
//    private static void asyncTest() {
//        System.out.println("Async test");
//        KinesisPublisher kp = new KinesisPublisher();
//        kp.publish("airtel-tv-stage-sync-api", "test", "test data");
//        try {
//            Thread.sleep(60 * 1000L);
//        } catch (Exception e) {
//
//        }
//    }
//
//    private static void syncTest() throws Exception {
//        KinesisPublisher kp = new KinesisPublisher();
//        kp.init();
//        ByteBuffer data = ByteBuffer.wrap("test data".getBytes(StandardCharsets.UTF_8));
//        Future<UserRecordResult> f;
//        f = kp.kinesisProducer.addUserRecord("airtel-tv-stage-sync-api", "test",  data);
//        UserRecordResult result = f.get(); // this does block
//        if (result.isSuccessful()) {
//            System.out.println("Put record into shard " + result.getShardId()+", "+result.getSequenceNumber());
//        } else {
//            System.out.println("ERROR");
//            for (Attempt attempt : result.getAttempts()) {
//                System.out.println(attempt);
//            }
//        }
//    }

}
