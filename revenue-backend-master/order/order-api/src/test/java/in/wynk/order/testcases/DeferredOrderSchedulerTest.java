package in.wynk.order.testcases;


import in.wynk.http.config.HttpClientConfig;
import in.wynk.order.OrderApiTestApplication;
import in.wynk.order.scheduler.DeferredOrdersScheduler;
import in.wynk.queue.producer.SQSMessagePublisher;
import in.wynk.queue.service.impl.SqsManagerServiceImpl;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test-scheduler.yml")
@SpringBootTest(classes = {OrderApiTestApplication.class, HttpClientConfig.class})
@ActiveProfiles("test-scheduler")
@Transactional
public class DeferredOrderSchedulerTest {

    @Autowired
    private DeferredOrdersScheduler deferredOrdersScheduler;

    @MockBean(name = in.wynk.queue.constant.BeanConstant.SQS_EVENT_PRODUCER)
    private SQSMessagePublisher sqsMessagePublisher;

    @InjectMocks
    private SqsManagerServiceImpl sqsManagerService;

    @Before
    @SneakyThrows
    public void setUp() {
        Mockito.when(sqsMessagePublisher.publish(Mockito.any())).thenReturn("SUCCESS");
    }

    @Test
    public void deferredOrdersSchedulerTest() {
        // This test case is run in debugger mode and seen how many deferred Messages are pushed to the queue
        deferredOrdersScheduler.processDeferredOrders();
    }
}
