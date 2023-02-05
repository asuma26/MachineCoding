package in.wynk.thanks.publisher;

import in.wynk.thanks.dto.ThanksSegmentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AtvThanksPublisher extends ThanksKinesisPublisher<ThanksSegmentDTO> {
    @Value("${atv.thanks.kinesis.downstream}")
    private String atvKinesisStreamName;

    @Override
    public String getStreamName() {
        return atvKinesisStreamName;
    }
}
