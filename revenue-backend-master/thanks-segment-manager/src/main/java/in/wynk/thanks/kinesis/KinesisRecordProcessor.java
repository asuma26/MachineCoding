package in.wynk.thanks.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ThrottlingException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.clientlibrary.types.ExtendedSequenceNumber;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import in.wynk.thanks.logging.ThanksLoggingMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public class KinesisRecordProcessor implements IRecordProcessor {

    private static final Logger logger = LoggerFactory.getLogger(KinesisRecordProcessor.class);
    private static final int NUM_RETRIES = 10;
    private static final long BACKOFF_TIME_IN_MILLIS = 1000L;
    private static final long CHECKPOINT_INTERVAL_MILLIS = 60000L;

    private String kinesisShardId;
    private ExtendedSequenceNumber shardExtendedSequenceNumber;
    private long nextCheckpointTimeInMillis;
    private String applicationName;

    private final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

    public KinesisRecordProcessor(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        this.kinesisShardId = initializationInput.getShardId();
        this.shardExtendedSequenceNumber = initializationInput.getExtendedSequenceNumber();
        //initializationInput.getPendingCheckpointSequenceNumber()
        logger.info("Initializing record processor for shardId = {}, extendedSequenceNumber = {}, subSequenceNumber = {}",
                this.kinesisShardId, this.shardExtendedSequenceNumber.getSequenceNumber(), this.shardExtendedSequenceNumber.getSubSequenceNumber());
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        logger.info("Processing {} records from shard {}", processRecordsInput.getRecords().size(), kinesisShardId);

        // Process records and perform all exception handling.
        processRecordsWithRetries(processRecordsInput.getRecords());

        // Checkpoint once every checkpoint interval.
        if (System.currentTimeMillis() > nextCheckpointTimeInMillis) {
            checkpoint(processRecordsInput.getCheckpointer());
            nextCheckpointTimeInMillis = System.currentTimeMillis() + CHECKPOINT_INTERVAL_MILLIS;
        }
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        logger.info("Shutting down record processor for shard: {}", kinesisShardId);
        // Important to checkpoint after reaching end of shard, so we can start processing data from child shards.
        if (shutdownInput.getShutdownReason() == ShutdownReason.TERMINATE) {
            checkpoint(shutdownInput.getCheckpointer());
        }
    }

    /**
     * Process records performing retries as needed
     *
     * @param records Data records to be processed.
     */
    private void processRecordsWithRetries(List<Record> records) {
        for (Record record : records) {
            boolean processedSuccessfully = false;
            for (int i = 0; i < NUM_RETRIES; i++) {
                try {
                    processSingleRecord(record);
                    processedSuccessfully = true;
                    break;
                } catch (Throwable t) {
                    logger.warn("Caught throwable while processing record {}", record, t);
                }

                // backoff if we encounter an exception.
                try {
                    Thread.sleep(BACKOFF_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    logger.debug("Interrupted sleep", e);
                }
            }

            if (!processedSuccessfully) {
                logger.error(ThanksLoggingMarkers.KINESIS_HANDLE_ERROR, "Couldn't process record {}. Skipping the record.", record);
            }
        }
    }

    /**
     * Process a single record.
     *
     * @param record The record to be processed.
     */
    private void processSingleRecord(Record record) {
        String data = null;
        try {
            data = decoder.decode(record.getData()).toString();
            logger.info("Kinesis Data = {} and kinesis record is : {} and sequence number is : {}", data, record, record.getSequenceNumber());
            Set<IKinesisRecordHandler> handlers = KinesisRecordHandlerFactory.getAppRecordHandlers(this.applicationName);
            if (handlers != null) {
                for (IKinesisRecordHandler handler : handlers) {
                    try {
                        handler.handle(data);
                    } catch (Throwable t) {
                        logger.error(ThanksLoggingMarkers.KINESIS_HANDLE_ERROR, "Error in handler for data = {}", data, t);
                    }
                }
            }
        } catch (NumberFormatException e) {
            logger.info("Record does not match sample record format. Ignoring record with data; {}", data);
        } catch (CharacterCodingException e) {
            logger.error("Malformed data: {}", data, e);
        }
    }

    private void checkpoint(IRecordProcessorCheckpointer checkpointer) {
        logger.info("Checkpointing shard {}", kinesisShardId);
        for (int i = 0; i < NUM_RETRIES; i++) {
            try {
                checkpointer.checkpoint();
                break;
            } catch (ShutdownException se) {
                // Ignore checkpoint if the processor instance has been shutdown (fail over).
                logger.error(ThanksLoggingMarkers.KINESIS_HANDLE_ERROR, "Caught shutdown exception, skipping checkpoint.", se);
                break;
            } catch (ThrottlingException e) {
                // Backoff and re-attempt checkpoint upon transient failures
                if (i >= (NUM_RETRIES - 1)) {
                    logger.error("Checkpoint failed after " + (i + 1) + "attempts.", e);
                    break;
                } else {
                    logger.info("Transient issue when checkpointing - attempt " + (i + 1) + " of " + NUM_RETRIES, e);
                }
            } catch (InvalidStateException e) {
                // This indicates an issue with the DynamoDB table (check for table, provisioned IOPS).
                logger.error("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library.", e);
                break;
            }
            try {
                Thread.sleep(BACKOFF_TIME_IN_MILLIS);
            } catch (InterruptedException e) {
                logger.debug("Interrupted sleep", e);
            }
        }
    }
}
