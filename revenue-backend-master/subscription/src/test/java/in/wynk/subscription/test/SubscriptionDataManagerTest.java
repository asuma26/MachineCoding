package in.wynk.subscription.test;

import in.wynk.common.enums.WynkService;
import in.wynk.subscription.common.dto.PlanDTO;
import in.wynk.subscription.service.ISubscriptionDataManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubscriptionDataManagerTest {

    @Autowired
    private ISubscriptionDataManager subscriptionDataManager;

    @Test
    public void allPlansAtvTest() {
        String service = WynkService.AIRTEL_TV.getValue();
        List<PlanDTO> plans = subscriptionDataManager.allPlans(service);
        assert plans.size() > 0;
    }

    @Test
    public void allPlansTest() {
        List<PlanDTO> plans = subscriptionDataManager.allPlans(null);
        assert plans.size() > 0;
    }

}
