package in.wynk.subscription.core.tests;

import in.wynk.common.enums.WynkService;
import in.wynk.data.config.WynkCassandraBuilder;
import in.wynk.data.config.WynkMongoDbFactoryBuilder;
import in.wynk.subscription.core.config.SubscriptionCoreConfig;
import in.wynk.subscription.core.config.UserdataConfig;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.core.service.IUserdataService;
import in.wynk.subscription.core.service.MigrationService;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.core.service.UserdataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@SpringBootTest(classes = {UserdataConfig.class, SubscriptionCoreConfig.class, UserdataService.class,
        MigrationService.class, SubscriptionCachingService.class, WynkMongoDbFactoryBuilder.class,
        WynkCassandraBuilder.class})
@TestPropertySource(locations = "classpath:application.properties")
@RunWith(SpringJUnit4ClassRunner.class)
public class UserDataServiceTest {

    @Autowired
    private IUserdataService userdataService;

    @Test
    public void getAllUserPlanDetailsTest() {
        String uid = "lViUuniOH80osYFqy0";
        WynkService airteltv = WynkService.AIRTEL_TV;
        List<UserPlanDetails> allUserPlanDetails = userdataService.getAllUserPlanDetails(uid, airteltv.getValue());
        assert allUserPlanDetails.size() > 0;
    }
}
