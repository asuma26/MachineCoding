package in.wynk.cache.manager;

import in.wynk.cache.ICacheManager;
import in.wynk.cache.cache.CompositeCache;
import in.wynk.cache.config.properties.CacheProperties;
import in.wynk.cache.config.properties.OperatingMode;
import in.wynk.cache.constant.BeanConstant;
import in.wynk.cache.entity.ICacheSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompositeCacheManager extends AbstractCompositeCacheManager implements ICacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CompositeCacheManager.class);

    @Autowired
    @Qualifier(BeanConstant.L1CACHE_MANAGER)
    private CacheManager l1CacheManager;
    @Autowired
    @Qualifier(BeanConstant.L2CACHE_MANAGER)
    private CacheManager l2CacheManager;

    private final CacheProperties cacheProperties;

    public CompositeCacheManager(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Override
    public Cache l1Cache(String key) {
        return l1CacheManager.getCache(key);
    }

    @Override
    public Cache l2Cache(String key) {
        return l2CacheManager.getCache(key);
    }

    @Override
    protected Cache decorateCache(Cache cache) {
        return cacheProperties.getCacheMode() == OperatingMode.COMPOSITE ?
                new CompositeCache(cache, l2Cache(cache.getName())) : cache;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return l1CacheManager.getCacheNames().stream()
                .map(l1CacheManager::getCache)
                .collect(Collectors.toSet());
    }

    protected Cache decorateCache(Cache cache, ICacheSpec cacheSpec) {
        return cacheProperties.getCacheMode() == OperatingMode.COMPOSITE ?
                new CompositeCache(cache, ((ICacheManager) this.l2CacheManager).createCache(cache.getName(), cacheSpec)) : cache;
    }

    @Override
    public Cache createCache(String key, ICacheSpec cacheSpec) {
        Cache cache = this.cacheMap.get(key);
        if (cache == null) {
            Cache missingCache = ((ICacheManager) this.l1CacheManager).createCache(key, cacheSpec);
            if (missingCache != null) {
                synchronized (this.cacheMap) {
                    cache = this.cacheMap.get(key);
                    if (cache == null) {
                        cache = this.decorateCache(missingCache, cacheSpec);
                        this.cacheMap.put(key, cache);
                        this.updateCacheNames(key);
                    }
                }
            }
        }
        return cache;
    }

    @Override
    public boolean containsCache(String key) {
        return this.cacheMap.containsKey(key);
    }

    @Override

    public Collection<String> getCacheNames() {
        return Collections.unmodifiableCollection(Stream.concat(this.cacheNames.stream(), super.getCacheNames().stream()).collect(Collectors.toList()));
    }

    private void updateCacheNames(String key) {
        Set<String> cacheNames = new LinkedHashSet<>(this.cacheNames.size() + 1);
        cacheNames.addAll(this.cacheNames);
        cacheNames.add(key);
        this.cacheNames = Collections.unmodifiableSet(cacheNames);
    }

}
