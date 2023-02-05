package in.wynk.cache.manager;

import in.wynk.cache.ICacheManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractCompositeCacheManager implements ICacheManager, InitializingBean {

    protected final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);
    protected volatile Set<String> cacheNames = Collections.emptySet();

    protected AbstractCompositeCacheManager() { }

    @Override
    public void afterPropertiesSet() {
        this.initializeCaches();
    }

    @Override
    public Cache getCache(@NonNull String key) {
        Cache cache = this.cacheMap.get(key);
        if (cache == null) {
            Cache missingCache = this.l1Cache(key);
            if (missingCache != null) {
                synchronized (this.cacheMap) {
                    cache = this.cacheMap.get(key);
                    if (cache == null) {
                        cache = this.decorateCache(missingCache);
                        this.cacheMap.put(key, cache);
                        this.updateCacheNames(key);
                    }
                }
            }
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.cacheNames;
    }

    protected abstract Cache l2Cache(String key);

    protected abstract Cache l1Cache(String key);

    protected abstract Cache decorateCache(Cache cache);

    protected abstract Collection<? extends Cache> loadCaches();

    private void initializeCaches() {
        Collection<? extends Cache> caches = this.loadCaches();
        synchronized (this.cacheMap) {
            this.cacheNames = Collections.emptySet();
            this.cacheMap.clear();
            Set<String> cacheNames = new LinkedHashSet<>(caches.size());

            for (Cache cache : caches) {
                String name = cache.getName();
                this.cacheMap.put(name, this.decorateCache(cache));
                cacheNames.add(name);
            }
            this.cacheNames = Collections.unmodifiableSet(cacheNames);
        }
    }

    private void updateCacheNames(String key) {
        Set<String> cacheNames = new LinkedHashSet<>(this.cacheNames.size() + 1);
        cacheNames.addAll(this.cacheNames);
        cacheNames.add(key);
        this.cacheNames = Collections.unmodifiableSet(cacheNames);
    }

}
