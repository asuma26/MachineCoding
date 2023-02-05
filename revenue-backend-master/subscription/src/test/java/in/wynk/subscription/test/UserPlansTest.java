package in.wynk.subscription.test;

import com.google.gson.Gson;
import in.wynk.common.dto.SessionDTO;
import in.wynk.subscription.dto.response.UserEligibleBenefits;
import in.wynk.subscription.service.IUserPlansService;
import in.wynk.subscription.test.utils.SubscriptionTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserPlansTest {

    @Autowired
    private IUserPlansService userPlansService;
    @Autowired
    private Gson gson;

    @Test
    public void eligiblePlanTest() {
        String msisdn = "1111111116";
        SessionDTO sessionDTO = SubscriptionTestUtils.dummyAtvSession(msisdn);
        UserEligibleBenefits.UserEligibleBenefitsData userEligiblePlans = userPlansService.getUserEligiblePlans(sessionDTO);
        System.out.println(gson.toJson(userEligiblePlans));
        assert !userEligiblePlans.getEligibleBenefits().isEmpty();
    }
}
