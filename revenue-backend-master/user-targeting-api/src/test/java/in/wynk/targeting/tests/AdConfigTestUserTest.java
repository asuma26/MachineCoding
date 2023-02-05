package in.wynk.targeting.tests;

import in.wynk.http.config.HttpClientConfig;
import in.wynk.targeting.TargetingApplication;
import in.wynk.targeting.core.dao.entity.mongo.AdsConfigTestUser;
import in.wynk.targeting.core.dao.repository.mongo.AdsTestUserRepository;
import in.wynk.targeting.services.MusicUserConfigService;
import in.wynk.targeting.services.PersonaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = {HttpClientConfig.class, TargetingApplication.class})
@RunWith(SpringRunner.class)
public class AdConfigTestUserTest {

    @Autowired
    private PersonaService personaService;
    @Autowired
    private MusicUserConfigService musicUserConfigService;
    @Autowired
    private AdsTestUserRepository adsTestUserRepository;

    @Test
    public void insertAdConfigTestUsers() {
        String uid = "W3LdJ4aQCadIkrqR60";
        AdsConfigTestUser testUser = AdsConfigTestUser.builder()
                .userPersona(personaService.getUserPersona(uid))
                .userconfig(musicUserConfigService.getUserConfig(uid))
                .id(uid).build();
        adsTestUserRepository.save(testUser);
    }

}
