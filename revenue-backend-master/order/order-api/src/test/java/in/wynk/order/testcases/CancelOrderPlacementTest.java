package in.wynk.order.testcases;

import in.wynk.common.enums.PaymentEvent;
import in.wynk.http.config.HttpClientConfig;
import in.wynk.order.OrderApiTestApplication;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.common.enums.OrderStatusDetail;
import in.wynk.order.core.constant.BeanConstant;
import in.wynk.order.core.dao.repository.IOrderDao;
import in.wynk.order.dto.response.OrderNotificationResponse;
import in.wynk.order.dto.response.OrderResponse;
import in.wynk.order.mock.OrderMockConstants;
import in.wynk.order.mock.OrderMockData;
import in.wynk.order.service.IOrderManager;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OrderApiTestApplication.class, HttpClientConfig.class})
public class CancelOrderPlacementTest {

    @Value("${service.subscription.api.endpoint.unSubscribePlan}")
    private String subscribePlanEndPoint;

    @MockBean(name = BeanConstant.SUBSCRIPTION_S2S_REST_TEMPLATE)
    private RestTemplate restTemplate;

    @MockBean
    private IOrderDao orderDao;

    @Autowired
    private IOrderManager orderManager;

    @Before
    @SneakyThrows
    public void setup() {
        Mockito.when(orderDao.save(Mockito.any())).thenReturn(OrderMockData.buildCancellationPersistableFulfilledOrder());
        Mockito.when(orderDao.findById(Mockito.eq(OrderMockConstants.MOCK_ORDER_ID))).thenReturn(Optional.ofNullable(OrderMockData.buildCancellationPersistableFulfilledOrder()));
        Mockito.when(restTemplate.postForObject(Mockito.eq(subscribePlanEndPoint), Mockito.any(), Mockito.eq(PlanProvisioningResponse.class))).thenReturn(OrderMockData.buildUnSubscriptionProvisioningResponse());
    }

    @Test
    @Order(1)
    public void run() {
        OrderResponse orderResponse = orderManager.placeOrder(OrderMockConstants.DUMMY_PARTNER, OrderMockData.buildCancellationOrderPlacementRequest());
        Assert.assertNotNull(orderResponse);
        Assert.assertTrue(orderResponse instanceof OrderNotificationResponse);
        Assert.assertEquals(orderResponse.getMsisdn(), OrderMockConstants.MSISDN);
        Assert.assertEquals(orderResponse.getOrderId(), OrderMockConstants.MOCK_ORDER_ID);
        Assert.assertEquals(((OrderNotificationResponse) orderResponse).getOrderDetails().getStatus(), OrderStatus.FULFILLED);
        Assert.assertEquals(((OrderNotificationResponse) orderResponse).getOrderDetails().getType(), PaymentEvent.UNSUBSCRIBE);
        Assert.assertEquals(((OrderNotificationResponse) orderResponse).getOrderDetails().getStatusDetail(), OrderStatusDetail.ORDER_SUC203);
        Assert.assertEquals(((OrderNotificationResponse) orderResponse).getPlanDetails().getPlanId(), OrderMockConstants.SELECTED_PLAN_ID);
        Assert.assertTrue(((OrderNotificationResponse) orderResponse).getPlanDetails().getEndDate() > System.currentTimeMillis());

    }

}
