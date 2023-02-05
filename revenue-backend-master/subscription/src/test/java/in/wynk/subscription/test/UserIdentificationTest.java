package in.wynk.subscription.test;

import com.google.gson.Gson;
import in.wynk.subscription.dto.request.MsisdnIdentificationRequest;
import in.wynk.subscription.dto.response.IdentificationResponse;
import in.wynk.subscription.service.IdentificationService;
import in.wynk.subscription.test.utils.SubscriptionTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserIdentificationTest {

    @Autowired
    private IdentificationService identificationService;
    @Autowired
    private Gson gson;

    @Test
    public void atvMobilityIdentification() throws Exception {
        String msisdn = "1111111111";
        MsisdnIdentificationRequest request = SubscriptionTestUtils.dummyAtvMobilityIdentificationRequest(msisdn);
        IdentificationResponse.MsisdnIdentificationResponse response = identificationService.identifyBestMsisdn(request);
        Assert.hasLength(response.getMsisdn(), "Msisdn cannot be empty");

    }

    @Test
    public void musicOfferProvisionTest() {
        String msisdn = "1111111151";

    }
}
