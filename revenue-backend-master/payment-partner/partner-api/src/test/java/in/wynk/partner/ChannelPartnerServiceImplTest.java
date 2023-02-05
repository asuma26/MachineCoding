package in.wynk.partner;

import com.google.gson.Gson;
import in.wynk.partner.common.dto.PartnerEligiblePlansResponse;
import in.wynk.partner.common.dto.UserActivePlansResponse;
import in.wynk.partner.data.ChannelPartnerServiceImplTestData;
import in.wynk.partner.listing.dto.response.ActivePlansListingResponse;
import in.wynk.partner.listing.dto.response.EligiblePlansListingResponse;
import in.wynk.partner.listing.service.IListingService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * @author Abhishek
 * @created 19/09/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ChannelPartnerServiceImplTest {

    private final Gson gson = new Gson();
    @MockBean
    private RestTemplate restTemplate;
    @Autowired
    private IListingService listingService;

    @SneakyThrows
    @Test
    public void testGetAllPlans() {
        Mockito.when(restTemplate.exchange(Mockito.any(RequestEntity.class), Mockito.eq(PartnerEligiblePlansResponse.class))).thenReturn(ChannelPartnerServiceImplTestData.getResponseEntityPartnerEligiblePlansResponse());
        EligiblePlansListingResponse response = listingService.getAllPlansToBeListed(ChannelPartnerServiceImplTestData.eligiblePlansListingRequest());
        JSONAssert.assertEquals(gson.toJson(ChannelPartnerServiceImplTestData.eligiblePlansListingResponse()), gson.toJson(response), false);
    }

    @SneakyThrows
    @Test
    public void testGetActivePlansForUser() {
        Mockito.when(restTemplate.exchange(Mockito.any(RequestEntity.class), Mockito.eq(UserActivePlansResponse.class))).thenReturn(ChannelPartnerServiceImplTestData.getResponseEntityUserActivePlansResponse());
        ActivePlansListingResponse response = listingService.getAllActivePlansForUser(ChannelPartnerServiceImplTestData.activePlansListingRequest());
        JSONAssert.assertEquals(gson.toJson(ChannelPartnerServiceImplTestData.activePlansListingResponse()), gson.toJson(response), false);
    }

    @org.junit.Test
    public void oldJunitTest() {

    }
}
