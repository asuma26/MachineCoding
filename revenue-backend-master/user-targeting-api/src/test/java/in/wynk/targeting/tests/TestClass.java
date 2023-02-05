package in.wynk.targeting.tests;

import in.wynk.targeting.services.CampaignTargetingFromCassandraTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClass {
    @Autowired
    private CampaignTargetingFromCassandraTask campaignTargetingFromCassandraTask;

    @Test
    public void testCassandraQuery() throws InterruptedException {
        String uid = "";
        String requestId = "adv";
        System.out.println(campaignTargetingFromCassandraTask.readCampaignTargeting(uid));
    }
}
