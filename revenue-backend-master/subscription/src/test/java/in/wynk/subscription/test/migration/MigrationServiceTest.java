package in.wynk.subscription.test.migration;

import in.wynk.http.config.HttpClientConfig;
import in.wynk.payment.common.messages.PaymentRecurringSchedulingMessage;
import in.wynk.queue.service.ISqsManagerService;
import in.wynk.subscription.SubscriptionApplication;
import in.wynk.subscription.core.dao.entity.Subscription;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.core.service.MigrationService;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.core.service.UserdataService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = {SubscriptionApplication.class, HttpClientConfig.class})
@RunWith(SpringRunner.class)
public class MigrationServiceTest {

    @MockBean
    private UserdataService userdataService;

    @MockBean
    private SubscriptionCachingService subscriptionCachingService;

    @MockBean
    private ISqsManagerService sqsManagerService;

    @Autowired
    private MigrationService migrationService;

    @BeforeEach
    public void setup() {
        Mockito.when(subscriptionCachingService.getOffers()).thenReturn(MigrationTestData.getOffers());
        Mockito.when(subscriptionCachingService.containsPlan(MigrationTestData.NEW_PLAN_ID)).thenReturn(true);
        Mockito.when(subscriptionCachingService.containsOffer(MigrationTestData.NEW_OFFER_ID)).thenReturn(true);
        Mockito.when(subscriptionCachingService.getPlan(MigrationTestData.NEW_PLAN_ID)).thenReturn(MigrationTestData.getNewPlan());
        Mockito.when(subscriptionCachingService.getOffer(MigrationTestData.NEW_OFFER_ID)).thenReturn(MigrationTestData.getNewOffer());
        Mockito.when(subscriptionCachingService.getOldPackToNewPlanMapping()).thenReturn(MigrationTestData.getOldPackToNewPlanMapping());
        Mockito.when(subscriptionCachingService.getProduct(MigrationTestData.NEW_PRODUCT_ID)).thenReturn(MigrationTestData.getNewProduct());
        Mockito.when(subscriptionCachingService.getOldOfferIdToNewOfferPlanMapping()).thenReturn(MigrationTestData.getOldOfferIdToNewOfferPlanMapping());
        Mockito.when(userdataService.getAllOfferMsisdnMapping(MigrationTestData.AIRTELTV, MigrationTestData.getMSISDN_VALUE())).thenReturn(MigrationTestData.getOfferMsisdnMappings());
        Mockito.doNothing().when(sqsManagerService).publishSQSMessage(Mockito.any(PaymentRecurringSchedulingMessage.class));
        Mockito.doNothing().when(userdataService).addAllSubscriptions(Mockito.anyString(), Mockito.anyString(), Mockito.anyCollection());
        Mockito.doNothing().when(userdataService).addAllUserPlanDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyCollection());
    }
    @Test
    public void testMigrateSubscriptionsForOneTimePaidSubscriptions() {
        List<Subscription> existingSubs = MigrationTestData.getOneTimePaidSubscriptionsForMigration();
        List<Subscription> modifiedSubs = migrationService.migrateSubscriptions(MigrationTestData.getUID(), MigrationTestData.AIRTELTV, existingSubs);
        Assert.assertTrue(existingSubs.size() < modifiedSubs.size());
    }

    @Test
    public void testMigrateSubscriptionsForRecurringPaidSubscriptions() {
        List<Subscription> existingSubs = MigrationTestData.getRecurringPaidSubscriptionsForMigration();
        List<Subscription> modifiedSubs = migrationService.migrateSubscriptions(MigrationTestData.getUID(), MigrationTestData.AIRTELTV, existingSubs);
        Assert.assertTrue(existingSubs.size() < modifiedSubs.size());
    }

    @Test
    public void testMigrateSubscriptionsForPayUSubscription() {
        List<Subscription> existingSubs = MigrationTestData.getPayuRecurringPaidSubscriptionForMigration();
        List<Subscription> modifiedSubs = migrationService.migrateSubscriptions(MigrationTestData.getUID(), MigrationTestData.AIRTELTV, existingSubs);
        Assert.assertTrue(existingSubs.size() < modifiedSubs.size());
    }

    @Test
    public void testMigrateEmptyUserPlanDetails() {
        List<UserPlanDetails> existing = MigrationTestData.getEmptyUserPlanDetails();
        List<UserPlanDetails> modified = migrationService.migrateUserPlanDetails(MigrationTestData.getUID(), MigrationTestData.getAIRTELTV(), MigrationTestData.getMSISDN_VALUE(), existing);
        Assert.assertTrue(modified.size() == 2);
    }

    @Test
    public void testMigrateWithPartialUserPlanDetails() {
        List<UserPlanDetails> existing = MigrationTestData.getPartialUserPlanDetails();
        List<UserPlanDetails> modified = migrationService.migrateUserPlanDetails(MigrationTestData.getUID(), MigrationTestData.getAIRTELTV(), MigrationTestData.getMSISDN_VALUE(), existing);
        Assert.assertTrue(modified.size() == 2);
    }

    @Test
    public void testFullyMigratedUserPlanDetails() {
        List<UserPlanDetails> existing = MigrationTestData.getMigratedUserPlanDetails();
        List<UserPlanDetails> modified = migrationService.migrateUserPlanDetails(MigrationTestData.getUID(), MigrationTestData.getAIRTELTV(), MigrationTestData.getMSISDN_VALUE(), existing);
        Assert.assertTrue(modified.size() == 2);
    }

    @org.junit.Test
    public void testJunitVintage() {

    }


}
