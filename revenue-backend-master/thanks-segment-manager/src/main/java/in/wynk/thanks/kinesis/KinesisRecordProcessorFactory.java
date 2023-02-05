package in.wynk.thanks.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class KinesisRecordProcessorFactory implements IRecordProcessorFactory {

    private String applicationName;

    public KinesisRecordProcessorFactory(String applicationName) {
        this.applicationName = applicationName;
    }

    public KinesisRecordProcessorFactory() {
        super();
    }

    @Override public IRecordProcessor createProcessor() {
        return new KinesisRecordProcessor(applicationName);
    }
}
