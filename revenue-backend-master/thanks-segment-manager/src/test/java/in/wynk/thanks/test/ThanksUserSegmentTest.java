package in.wynk.thanks.test;

import com.google.gson.Gson;
import in.wynk.common.utils.BCEncryptor;
import in.wynk.common.utils.EncryptionUtils;
import in.wynk.common.utils.BCEncryptor;
import in.wynk.thanks.dto.ThanksSegmentDTO;
import in.wynk.thanks.kinesis.KinesisPublisher;
import in.wynk.thanks.service.ThanksSegmentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.IntStream;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ThanksUserSegmentTest {
    private static final String testATVThanksData = "{\"servicePack\":\"ATVPLUS\",\"si\":\"F5udRcW3XcBjh3mtzu+q6w==\",\"source\":\"THANKS\",\"event\":\"Activation\",\"timestamp\":\"1588204975487\"}";
    private static final String testMUSICThanksData = "{\"servicePack\":\"WYNK_PLATINUM\",\"si\":\"CBHuhY0Ipf1Q1bCbJqFrFw==\",\"source\":\"THANKS\",\"event\":\"Activation\",\"timestamp\":\"1588204975487\"}";

    @Autowired
    private KinesisPublisher publisher;
    @Autowired
    private ThanksSegmentService thanksSegmentService;
    @Value("${kinesis.test.stream.name}")
    private String streamName;
    @Autowired
    private Gson gson;
    @Value("${user.wcf-thanks.key}")
    private String thanksToken;
    @Value("${user.token.key}")
    private String uidToken;

    @Test
    public void testProducer() throws InterruptedException {
        String partitionKey = "test";
        IntStream.range(0, 1000).forEach(i -> {
            if (i % 2 == 0) {
                publisher.publish(streamName, partitionKey, testATVThanksData);
            } else {
                publisher.publish(streamName, partitionKey, testMUSICThanksData);
            }
        });
        Thread.sleep(10000);
    }

    @Test
    public void testThanksSegmentService() throws InterruptedException {
        thanksSegmentService.processRecords(testMUSICThanksData);
        Thread.sleep(10000L);
    }

    @Test
    public void testUpdateThanksDto() throws InterruptedException {
        ThanksSegmentDTO thanksSegmentDTO = gson.fromJson(testMUSICThanksData, ThanksSegmentDTO.class);
        thanksSegmentService.updateSegment(thanksSegmentDTO);
        Thread.sleep(10000L);
    }


    @Test
    public void cassandraInsertionTest() {

    }

    @Test
    public void decryptionTest() {
        String token = EncryptionUtils.generateAppToken(thanksToken, uidToken);
        String encSi = "F5udRcW3XcBjh3mtzu+q6w==";
        String si = BCEncryptor.decrypt(encSi, token);
        System.out.println(si);
    }


}
