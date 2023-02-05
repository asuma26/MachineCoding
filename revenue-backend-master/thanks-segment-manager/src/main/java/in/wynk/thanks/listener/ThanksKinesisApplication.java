package in.wynk.thanks.listener;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.metrics.impl.NullMetricsFactory;
import in.wynk.thanks.kinesis.KinesisRecordProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class ThanksKinesisApplication {

    private static final Logger logger = LoggerFactory.getLogger(ThanksKinesisApplication.class);

    private static Executor executor = Executors.newFixedThreadPool(4);
    @Value("${thanks.kinesis.stream}")
    private String applicationStreamName;
    @Value("${thanks.kinesis.app}")
    private String applicationName;

    private static final InitialPositionInStream APPLICATION_INITIAL_POSITION_IN_STREAM = InitialPositionInStream.TRIM_HORIZON;

    private static AWSCredentialsProvider credentialsProvider;

    private void init() {
        // Ensure the JVM will refresh the cached IP values of AWS resources (e.g. service endpoints).
        java.security.Security.setProperty("networkaddress.cache.ttl", "30");
        credentialsProvider = new DefaultAWSCredentialsProviderChain();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot load the credentials from the credential profiles file", e);
        }
    }

    @PostConstruct
    public void consume() throws UnknownHostException {
        init();
        String workerId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + UUID.randomUUID();
        KinesisClientLibConfiguration kinesisClientLibConfiguration = new KinesisClientLibConfiguration(this.applicationName, this.applicationStreamName, credentialsProvider, workerId).withRegionName("ap-south-1");
        kinesisClientLibConfiguration.withInitialPositionInStream(APPLICATION_INITIAL_POSITION_IN_STREAM);

        IRecordProcessorFactory recordProcessorFactory = new KinesisRecordProcessorFactory(this.applicationName);
        Worker worker = new Worker.Builder()
                .recordProcessorFactory(recordProcessorFactory)
                .config(kinesisClientLibConfiguration).metricsFactory(new NullMetricsFactory())
                .build();

        logger.info("Running {} to process stream {} as worker {}", this.applicationName, this.applicationStreamName, workerId);

        try {
            executor.execute(worker);
        } catch (Throwable t) {
            logger.error("Caught throwable while processing data {}", t.getMessage(), t);
        }
    }


}
