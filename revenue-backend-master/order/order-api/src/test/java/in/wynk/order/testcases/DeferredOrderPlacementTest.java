package in.wynk.order.testcases;

import in.wynk.common.enums.PaymentEvent;
import in.wynk.http.config.HttpClientConfig;
import in.wynk.order.OrderApiTestApplication;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.common.enums.OrderStatusDetail;
import in.wynk.order.core.constant.BeanConstant;
import in.wynk.order.core.dao.entity.DeferredOrderDetail;
import in.wynk.order.core.dao.repository.IDeferredOrderDetailsDao;
import in.wynk.order.core.dao.repository.IOrderDao;
import in.wynk.order.dto.response.OrderNotificationResponse;
import in.wynk.order.dto.response.OrderResponse;
import in.wynk.order.mock.OrderMockConstants;
import in.wynk.order.mock.OrderMockData;
import in.wynk.order.scheduler.DeferredOrdersScheduler;
import in.wynk.order.service.IDeferredService;
import in.wynk.order.service.IOrderManager;
import in.wynk.queue.producer.SQSMessagePublisher;
import in.wynk.queue.service.impl.SqsManagerServiceImpl;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest(classes = {OrderApiTestApplication.class, HttpClientConfig.class})
public class DeferredOrderPlacementTest {

    @Value("${service.subscription.api.endpoint.subscribePlan}")
    private String subscribePlanEndPoint;

    @MockBean(name = BeanConstant.SUBSCRIPTION_S2S_REST_TEMPLATE)
    private RestTemplate restTemplate;

    @MockBean
    private IOrderDao orderDao;

    @MockBean
    private IDeferredOrderDetailsDao deferredOrderDetailsDao;

    @Autowired
    private IOrderManager orderManager;

    @Autowired
    private DeferredOrdersScheduler deferredOrdersScheduler;

    @MockBean
    private IDeferredService orderDeferredServiceImpl;

    @MockBean(name = in.wynk.queue.constant.BeanConstant.SQS_EVENT_PRODUCER)
    private SQSMessagePublisher sqsMessagePublisher;

    @InjectMocks
    private SqsManagerServiceImpl sqsManagerService;

    @Before
    @SneakyThrows
    public void setup() {
        Mockito.when(orderDao.save(Mockito.any())).thenReturn(OrderMockData.buildPersistableFulfilledOrder());
        Mockito.when(deferredOrderDetailsDao.save(Mockito.any())).thenReturn(null);
        Mockito.when(orderDao.findById(Mockito.eq(OrderMockConstants.MOCK_ORDER_ID))).thenReturn(Optional.ofNullable(OrderMockData.buildPersistableAcknowledgedOrder()));
        Mockito.when(restTemplate.postForObject(Mockito.eq(subscribePlanEndPoint), Mockito.any(), Mockito.eq(PlanProvisioningResponse.class))).thenReturn(OrderMockData.buildDeferredSubscriptionProvisioningResponse());
        Mockito.doReturn(OrderMockData.buildDeferredOrdersData()).when(orderDeferredServiceImpl).getScheduledDeferredOrdersPaginated();
        Mockito.when(sqsMessagePublisher.publish(Mockito.any())).thenReturn("SUCCESS");
    }

    @Test
    @Order(1)
    public void run() {
        OrderResponse orderResponse = orderManager.placeOrder(OrderMockConstants.DUMMY_PARTNER, OrderMockData.buildFreshOrderPlacementRequest());
        Assert.assertNotNull(orderResponse);
        Assert.assertTrue(orderResponse instanceof OrderNotificationResponse);
        Assert.assertEquals(orderResponse.getMsisdn(), OrderMockConstants.MSISDN);
        Assert.assertEquals(orderResponse.getOrderId(), OrderMockConstants.MOCK_ORDER_ID);
        Assert.assertEquals(((OrderNotificationResponse) orderResponse).getOrderDetails().getStatus(), OrderStatus.FULFILLED);
        Assert.assertEquals(((OrderNotificationResponse) orderResponse).getOrderDetails().getType(), PaymentEvent.PURCHASE);
        Assert.assertEquals(((OrderNotificationResponse) orderResponse).getOrderDetails().getStatusDetail(), OrderStatusDetail.ORDER_SUC203);
        Assert.assertEquals(((OrderNotificationResponse) orderResponse).getPlanDetails().getPlanId(), OrderMockConstants.SELECTED_PLAN_ID);
        Assert.assertTrue(((OrderNotificationResponse) orderResponse).getPlanDetails().getEndDate() > System.currentTimeMillis());

    }

    @Test
    public void orderDeferredServiceImplTest() {
        List<DeferredOrderDetail> deferredOrderDetails =
                orderDeferredServiceImpl.getScheduledDeferredOrdersPaginated()
                        .collect(Collectors.toList());
        Assert.assertNotNull(deferredOrderDetails);
        Assert.assertTrue(deferredOrderDetails.size() == 2);
    }

    @Test
    public void deferredOrdersPushDeferredQueueTest() {
        deferredOrdersScheduler.sendToDeferredQueue(orderDeferredServiceImpl.getScheduledDeferredOrdersPaginated().collect(Collectors.toList()));
    }

}
