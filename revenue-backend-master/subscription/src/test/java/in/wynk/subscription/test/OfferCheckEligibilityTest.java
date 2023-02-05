package in.wynk.subscription.test;

import in.wynk.common.enums.WynkService;
import in.wynk.subscription.core.dao.entity.FilterUserDetails;
import in.wynk.subscription.core.dao.entity.Offer;
import in.wynk.subscription.core.dao.repository.filterUsers.IFilterDbDao;
import in.wynk.subscription.dto.OfferContext;
import in.wynk.subscription.dto.OfferEligibilityCheckRequest;
import in.wynk.subscription.enums.OfferEligibilityStatusReason;
import in.wynk.subscription.service.OfferCheckEligibility;
import in.wynk.subscription.test.data.OfferCheckEligibilityTestConstants;
import in.wynk.subscription.test.data.OfferCheckEligibilityTestData;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Abhishek
 * @created 28/09/20
 */
@SpringBootTest
public class OfferCheckEligibilityTest {
    @Mock
    private IFilterDbDao filterDbDao;
    @Mock
    private Offer offer;

    @InjectMocks
    private OfferCheckEligibility offerCheckEligibility;
    private OfferContext context;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        offerCheckEligibility = new OfferCheckEligibility();
        context = new OfferContext();
        context.setOffer(OfferCheckEligibilityTestData.offer());
        context.setMsisdn(OfferCheckEligibilityTestConstants.MSISDN);
        context.setService(WynkService.AIRTEL_TV);

    }

    @Test
    public void hasPlanActiveWithinDaysSuccessTest(){
        int days = 20;
        context.setUserPlanDetails(OfferCheckEligibilityTestData.successUserPlanDetails(days, 0));
        offerCheckEligibility.init(context);
        offerCheckEligibility.hasPlanActiveWithinDays(days, OfferCheckEligibilityTestConstants.PLAN_IDS);
        Assert.assertTrue(offerCheckEligibility.isEligible());
    }

    @Test
    public void hasPlanActiveWithinDaysFailureTest(){
        int days = 30;
        context.setUserPlanDetails(OfferCheckEligibilityTestData.failureUserPlanDetails(days, 0));
        offerCheckEligibility.init(context);
        offerCheckEligibility.hasPlanActiveWithinDays(days, OfferCheckEligibilityTestConstants.PLAN_IDS);
        Assert.assertFalse(offerCheckEligibility.isEligible());
    }

    @Test
    public void limitUserSuccessTest(){
        int days = 30;
        int planCount = 1;
        context.setUserPlanDetails(OfferCheckEligibilityTestData.successUserPlanDetails(days, planCount));
        context.setService(WynkService.AIRTEL_TV);
        offerCheckEligibility.init(context);
        offerCheckEligibility.limitUser(planCount + 1);
        Assert.assertTrue(offerCheckEligibility.isEligible());
    }

    @Test
    public void limitUserFailureTest() {
        int days = 30;
        int planCount = 2;
        context.setUserPlanDetails(OfferCheckEligibilityTestData.successUserPlanDetails(days, planCount));
        context.setService(WynkService.AIRTEL_TV);
        offerCheckEligibility.init(context);
        offerCheckEligibility.limitUser(planCount - 1);
        Assert.assertFalse(offerCheckEligibility.isEligible());


    }


    public void initializeWithMsisdn() {
        OfferEligibilityCheckRequest offerEligibilityCheckRequest = OfferEligibilityCheckRequest.builder().msisdn("9876543210")
                .build();
        OfferContext offerContext = new OfferContext(offerEligibilityCheckRequest);
        offerContext.setOffer(offer);
        offerCheckEligibility.init(offerContext);
    }


    private FilterUserDetails getGenericUser(String msisdn) {
        return FilterUserDetails.builder().msisdn(msisdn).build();
    }

    @Test
    public void shouldFetchUser() {
        Mockito.when(filterDbDao.getFilterUserDetails(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(getGenericUser("9876543210"));
        initializeWithMsisdn();
        offerCheckEligibility = offerCheckEligibility.belongsToGroup("testBlackWhiteListedUser");

        Assert.assertNotNull(offerCheckEligibility.isEligible());
        Assert.assertEquals(Boolean.TRUE, offerCheckEligibility.isEligible());
    }

    @Test
    public void blankCollectionPassedForFetchingUser() {
        Mockito.when(filterDbDao.getFilterUserDetails(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(getGenericUser("9876543210"));
        initializeWithMsisdn();
        offerCheckEligibility = offerCheckEligibility.belongsToGroup("");

        Assert.assertNotNull(offerCheckEligibility.isEligible());
        Assert.assertEquals(Boolean.FALSE, offerCheckEligibility.isEligible());
        Assert.assertNotNull(offerCheckEligibility.getReason());
        Assert.assertEquals(OfferEligibilityStatusReason.COLLECTION_NAME_REQUIRED, offerCheckEligibility.getReason());
    }

    @Test
    public void nullCollectionPassedForFetchingUser() {
        initializeWithMsisdn();
        Mockito.when(filterDbDao.getFilterUserDetails(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(getGenericUser("9876543210"));
        offerCheckEligibility = offerCheckEligibility.belongsToGroup(null);

        Assert.assertNotNull(offerCheckEligibility.isEligible());
        Assert.assertEquals(Boolean.FALSE, offerCheckEligibility.isEligible());
        Assert.assertNotNull(offerCheckEligibility.getReason());
        Assert.assertEquals(OfferEligibilityStatusReason.COLLECTION_NAME_REQUIRED, offerCheckEligibility.getReason());
    }
}
