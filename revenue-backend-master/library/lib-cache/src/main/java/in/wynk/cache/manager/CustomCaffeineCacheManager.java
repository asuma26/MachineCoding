package in.wynk.cache.manager;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import in.wynk.cache.ICacheManager;
import in.wynk.cache.entity.CompositeCacheSpec;
import in.wynk.cache.entity.ICacheSpec;
import in.wynk.cache.entity.LevelOneCacheSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomCaffeineCacheManager extends CaffeineCacheManager implements ICacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CustomCaffeineCacheManager.class);

    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

    @Nullable
    private CacheLoader<Object, Object> cacheLoader;

    public CustomCaffeineCacheManager() {
    }

    @Override
    public  Collection<String> getCacheNames() {
        return Collections.unmodifiableCollection(Stream.concat(this.cacheMap.keySet().stream(), super.getCacheNames().stream()).collect(Collectors.toList()));
    }

    @Override
    public org.springframework.cache.Cache createCache(String key, ICacheSpec cacheSpec) {
        Cache cache = this.cacheMap.get(key);
        if (cache == null) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(key);
                if (cache == null) {
                    cache = createCaffeineCache(key, cacheSpec);
                    this.cacheMap.put(key, cache);
                }
            }
        }
        return cache;
    }

    @Override
    public boolean containsCache(String name) {
        return this.cacheMap.containsKey(name);
    }

    @Override
    public Cache getCache(@NonNull String name) {
        return this.cacheMap.get(name);
    }

    protected Cache createCaffeineCache(String name, ICacheSpec cacheSpec) {
        return new CaffeineCache(name, createNativeCaffeineCache(name, cacheSpec), cacheSpec.<CompositeCacheSpec>getSpec().getL1CacheSpec().isNullable());
    }

    protected com.github.benmanes.caffeine.cache.Cache<Object, Object> createNativeCaffeineCache(String name, ICacheSpec cacheSpec) {
        LevelOneCacheSpec caffeineCacheSpec = (LevelOneCacheSpec) cacheSpec.<CompositeCacheSpec>getSpec().getL1CacheSpec();
        if (caffeineCacheSpec.getTtl() > 0) {
            Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                    .expireAfterWrite(caffeineCacheSpec.getTtl(), TimeUnit.SECONDS)
                    .expireAfterAccess(caffeineCacheSpec.getTtl(), TimeUnit.SECONDS);
            if (this.cacheLoader != null) {
                return caffeineBuilder.build(this.cacheLoader);
            } else {
                return caffeineBuilder.build();
            }
        } else {
            return super.createNativeCaffeineCache(name);
        }
    }

    public void setCacheLoader( CacheLoader<Object, Object> cacheLoader) {
        if (!ObjectUtils.nullSafeEquals(this.cacheLoader, cacheLoader)) {
            this.cacheLoader = cacheLoader;
            refreshKnownCaches();
        }
    }

    private void refreshKnownCaches() {
        for (Map.Entry<String, Cache> entry : this.cacheMap.entrySet()) {
            entry.setValue(createCaffeineCache(entry.getKey()));
        }
    }

}
