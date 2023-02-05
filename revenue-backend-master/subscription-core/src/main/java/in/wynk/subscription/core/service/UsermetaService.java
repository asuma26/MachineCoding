package in.wynk.subscription.core.service;

import in.wynk.cache.aspect.advice.CacheEvict;
import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.subscription.core.dao.entity.ThanksUserSegment;
import in.wynk.subscription.core.dao.repository.usermeta.ThanksSegmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static in.wynk.cache.constant.BeanConstant.L2CACHE_MANAGER;

@Slf4j
@Service
public class UsermetaService implements IUsermetaService {

    @Autowired
    private ThanksSegmentRepository thanksSegmentRepository;

    private Map<String, List<ThanksUserSegment>> getAllThankSegments(Set<String> allSi) {
        Map<String, List<ThanksUserSegment>> segments = thanksSegmentRepository.getAllSegments(allSi);
        log.info("All Thanks User Segments: {}", segments);
        return segments;
    }

    @Override
    @Cacheable(cacheName = "THANKS_SEGMENT", cacheKey = "#msisdn", l2CacheTtl = 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public Map<String, List<ThanksUserSegment>> getAllThankSegments(String msisdn, Set<String> allSi) {
        allSi.add(msisdn);
        return getAllThankSegments(allSi);
    }

    @Override
    @CacheEvict(cacheName = "THANKS_SEGMENT", cacheKey = "#msisdn", l2CacheTtl = 24 * 60 * 60, cacheManager = L2CACHE_MANAGER)
    public void saveThanksUserSegment(String msisdn, ThanksUserSegment thanksUserSegment) {
        thanksSegmentRepository.save(thanksUserSegment);
    }

}
