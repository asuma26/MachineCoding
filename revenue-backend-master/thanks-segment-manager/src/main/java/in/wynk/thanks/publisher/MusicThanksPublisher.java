package in.wynk.thanks.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MusicThanksPublisher extends ThanksKinesisPublisher<MusicThanksPublisher.MusicThanksSegment> {
    @Value("${music.thanks.kinesis.downstream}")
    private String musicKinesisStreamName;

    @Override
    public String getStreamName() {
        return musicKinesisStreamName;
    }

    public static class MusicThanksSegment {
        private String uid;
        private String si;
        private Long timestamp;

        public MusicThanksSegment(String uid, String si) {
            this.uid = uid;
            this.si = si;
            this.timestamp = System.currentTimeMillis();
        }

        public String getSi() {
            return si;
        }

        public String getUid() {
            return uid;
        }

        public Long getTimestamp() {
            return timestamp;
        }
    }
}
