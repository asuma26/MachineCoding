package in.wynk.targeting.services;

import in.wynk.targeting.core.dao.entity.mongo.AdsConfigTestUser;
import in.wynk.targeting.core.dao.repository.mongo.AdsTestUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static in.wynk.common.constant.BaseConstants.IN_MEMORY_CACHE_CRON;

@Service
@Slf4j
public class UTCachingService {

    private static final Map<String, AdsConfigTestUser> testingAdConfigContext = new ConcurrentHashMap<>();
    @Autowired
    private AdsTestUserRepository adsTestUserRepository;

    @PostConstruct
    @Scheduled(fixedDelay = IN_MEMORY_CACHE_CRON, initialDelay = IN_MEMORY_CACHE_CRON)
    public void init() {
        loadTestingConfig();
    }

    private void loadTestingConfig() {
        List<AdsConfigTestUser> testUsers = getAllTestUsers();
        Map<String, AdsConfigTestUser> testUsersMap = testUsers.stream().collect(Collectors.toMap(AdsConfigTestUser::getId, Function.identity()));
        testingAdConfigContext.putAll(testUsersMap);
    }

    private List<AdsConfigTestUser> getAllTestUsers() {
        return adsTestUserRepository.findAll();
    }

    public boolean isTestUser(String uid) {
        return testingAdConfigContext.containsKey(uid);
    }

    public AdsConfigTestUser getUserContext(String uid) {
        return testingAdConfigContext.get(uid);
    }

}
