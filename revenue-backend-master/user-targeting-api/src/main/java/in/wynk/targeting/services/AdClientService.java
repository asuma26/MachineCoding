package in.wynk.targeting.services;

import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.cache.constant.BeanConstant;
import in.wynk.targeting.core.dao.entity.mongo.AdClient;
import in.wynk.targeting.core.dao.entity.mongo.WynkClient;
import in.wynk.targeting.core.dao.repository.mongo.AdClientsMongoRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@NoArgsConstructor
public class AdClientService {

    @Autowired
    private AdClientsMongoRepository adClientMongoRepository;

    @Cacheable(cacheName = "AD_CLIENT", cacheKey = "T(java.lang.String).format('%s:%s:%s', #root.methodName, #client.id, #client.os)", cacheManager = BeanConstant.L1CACHE_MANAGER, l1CacheTtl = 60 * 60)
    public AdClient findByClientId(WynkClient client) {
        return adClientMongoRepository.findByClientId(client.getId())
                .orElseThrow(() -> new NoSuchElementException(client.getClientId() + " not supported"));
    }

}
