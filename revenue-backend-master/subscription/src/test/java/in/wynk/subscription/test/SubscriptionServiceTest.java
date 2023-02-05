package in.wynk.subscription.test;

import in.wynk.common.enums.TransactionStatus;
import in.wynk.common.enums.WynkService;
import in.wynk.subscription.core.dao.entity.Subscription;
import in.wynk.subscription.dto.SubscriptionProvisionRequest;
import in.wynk.subscription.dto.SubscriptionUnProvisionRequest;
import in.wynk.subscription.service.ISubscriptionService;
import in.wynk.subscription.service.PaymentService;
import in.wynk.subscription.test.utils.SubscriptionTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static in.wynk.common.enums.PaymentEvent.PURCHASE;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubscriptionServiceTest {

    private static final String SUCCESS_SID = "SUCCESS_SID";
    private static final String FAILED_SID = "FAILED_SID";
    private static final String PENDING_SID = "PENDING_SID";
    private static final Integer ONE_TIME_PLAN_ID = 1000182;
    private static final Integer SUBSCRIPTION_PLAN_ID = 1000180;
    private static final String DUMMY_TXN_ID = UUID.randomUUID().toString();


    @Autowired
    private ISubscriptionService subscriptionService;

    @MockBean
    private PaymentService paymentService;

    @Before
    public void setup(){
        Mockito.doReturn(TransactionStatus.SUCCESS).when(paymentService).fetchTransactionStatus(SUCCESS_SID);
        Mockito.doReturn(TransactionStatus.FAILURE).when(paymentService).fetchTransactionStatus(FAILED_SID);
        Mockito.doReturn(TransactionStatus.INPROGRESS).when(paymentService).fetchTransactionStatus(PENDING_SID);
    }

    @Test
    public void provisionSubscriptionTest() {
        SubscriptionProvisionRequest request = SubscriptionTestUtils.dummySubscriptionProvisioningRequest(PURCHASE, DUMMY_TXN_ID, WynkService.AIRTEL_TV);
        List<Subscription> subscriptionStatuses = subscriptionService.provision(request);
        System.out.println(subscriptionStatuses);
        assert !subscriptionStatuses.isEmpty();
    }

    @Test
    public void unProvisionSubscriptionTest() {
        SubscriptionUnProvisionRequest request = SubscriptionTestUtils.dummySubscriptionUnProvisioningRequest(ONE_TIME_PLAN_ID, WynkService.AIRTEL_TV);
        subscriptionService.unProvision(request);
        assert true;
    }

}
