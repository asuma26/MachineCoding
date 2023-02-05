package in.wynk.subscription.core.tests;

import in.wynk.data.config.WynkCassandraBuilder;
import in.wynk.subscription.core.config.UsermetaConfig;
import in.wynk.subscription.core.service.IUsermetaService;
import in.wynk.subscription.core.service.UsermetaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@SpringBootTest(classes = {UsermetaConfig.class, UsermetaService.class, WynkCassandraBuilder.class})
@TestPropertySource(locations = "classpath:application.properties")
@RunWith(SpringJUnit4ClassRunner.class)
public class UserMetaServiceTest {

    @Autowired
    private IUsermetaService userMetaService;

    @Test
    public void test() {
    }
}
