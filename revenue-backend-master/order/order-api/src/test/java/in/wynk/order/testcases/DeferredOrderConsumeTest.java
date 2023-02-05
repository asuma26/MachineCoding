package in.wynk.order.testcases;


import in.wynk.http.config.HttpClientConfig;
import in.wynk.order.OrderApiTestApplication;
import in.wynk.order.dto.request.OrderDeferredFulfilRequest;
import in.wynk.order.mock.OrderMockConstants;
import in.wynk.order.service.IOrderManager;
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


@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test-scheduler.yml")
@SpringBootTest(classes = {OrderApiTestApplication.class, HttpClientConfig.class})
@ActiveProfiles("test-scheduler")
public class DeferredOrderConsumeTest {

    @MockBean(name = in.wynk.queue.constant.BeanConstant.SQS_EVENT_PRODUCER)
    private SQSMessagePublisher sqsMessagePublisher;

    @InjectMocks
    private SqsManagerServiceImpl sqsManagerServiceImpl;

    @Autowired
    private IOrderManager orderManager;

    @Before
    @SneakyThrows
    public void setUp() {
        Mockito.when(sqsMessagePublisher.publish(Mockito.any())).thenReturn("SUCCESS");
    }

    @Test
    public void deferredOrderFulfilTest() {
        // to test if order is picked correctly according to order id passed.
        // This function call to be tested using debugger
        orderManager.fulfilDeferredOrder(OrderDeferredFulfilRequest.builder().orderId(OrderMockConstants.MOCK_ORDER_ID).build());
    }
}
