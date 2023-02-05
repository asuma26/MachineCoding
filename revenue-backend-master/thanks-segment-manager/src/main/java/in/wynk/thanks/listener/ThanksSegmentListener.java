package in.wynk.thanks.listener;

import com.google.gson.Gson;
import in.wynk.thanks.kinesis.IKinesisRecordHandler;
import in.wynk.thanks.kinesis.KinesisRecordHandlerFactory;
import in.wynk.thanks.logging.ThanksLoggingMarkers;
import in.wynk.thanks.service.ThanksSegmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ThanksSegmentListener implements IKinesisRecordHandler {

    private static final Logger logger = LoggerFactory.getLogger(ThanksSegmentListener.class);
    @Value("${thanks.kinesis.app}")
    private String applicationName;
    @Autowired
    private ThanksSegmentService thanksSegmentService;
    @Autowired
    private Gson gson;

    @PostConstruct
    public void init() {
        register();
    }

    @Override
    public void handle(String data) {
        try {
            thanksSegmentService.processRecords(data);
        } catch (Exception e) {
            logger.error(ThanksLoggingMarkers.KINESIS_HANDLE_ERROR, "Error while handling download event, data: {}", data, e);
        }
    }

    @Override
    public void register() {
        KinesisRecordHandlerFactory.registerListener(this);
    }

    @Override
    public String getApplicationName() {
        return this.applicationName;
    }
}

