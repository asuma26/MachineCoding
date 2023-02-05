package in.wynk.subscription.test;

import com.google.gson.Gson;
import in.wynk.common.enums.AppId;
import in.wynk.subscription.dto.OfferProvisionRequest;
import in.wynk.subscription.dto.response.OfferProvisionResponse;
import in.wynk.subscription.service.impl.OfferProcessingService;
import in.wynk.subscription.test.utils.SubscriptionTestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OfferProvisioningTest {

    @Autowired
    private OfferProcessingService offerProcessingService;
    @Autowired
    private Gson gson;

    @Test
    public void atvOfferProvisionTest(){
        String msisdn = "1111111105";
        OfferProvisionRequest request = SubscriptionTestUtils.dummyATVOfferProvisionRequest(msisdn);
        OfferProvisionResponse.OfferProvisionData offerProvisionResponse = offerProcessingService.provisionOffers(request);
        System.out.println(gson.toJson(offerProvisionResponse));
        assert CollectionUtils.isNotEmpty(offerProvisionResponse.getSubscriptionStatus());
    }

    @Test
    public void atvOfferProvisionLargeScreenTest(){
        String msisdn = "1111111153";
        OfferProvisionRequest request = SubscriptionTestUtils.dummyATVOfferProvisionRequest(msisdn, AppId.LARGESCREEN);
        OfferProvisionResponse.OfferProvisionData offerProvisionResponse = offerProcessingService.provisionOffers(request);
        System.out.println(gson.toJson(offerProvisionResponse));
        assert CollectionUtils.isNotEmpty(offerProvisionResponse.getSubscriptionStatus());
    }

    @Test
    public void atvOfferProvisionLimitDeviceTest(){
        String msisdn = "1111111101";
        String device = "abcd1234";
        OfferProvisionRequest request = SubscriptionTestUtils.dummyATVOfferProvisionRequest(msisdn, device);
        OfferProvisionResponse.OfferProvisionData offerProvisionResponse = offerProcessingService.provisionOffers(request);
        System.out.println(gson.toJson(offerProvisionResponse));
        assert CollectionUtils.isNotEmpty(offerProvisionResponse.getSubscriptionStatus());
    }

    @Test
    public void musicOfferProvisionTest(){
        String msisdn = "1111111151";
        OfferProvisionRequest request = SubscriptionTestUtils.dummyMusicOfferProvisionRequest(msisdn);
        OfferProvisionResponse.OfferProvisionData offerProvisionResponse = offerProcessingService.provisionOffers(request);
        System.out.println(gson.toJson(offerProvisionResponse));
        assert CollectionUtils.isNotEmpty(offerProvisionResponse.getSubscriptionStatus());
    }
}
