package in.wynk.cache.manager;

import in.wynk.cache.ICacheManager;
import in.wynk.cache.cache.ShardedRedisCache;
import in.wynk.cache.entity.CompositeCacheSpec;
import in.wynk.cache.entity.ICacheSpec;
import in.wynk.cache.entity.LevelTwoCacheSpec;
import in.wynk.cache.factory.IShardedRedisPoolFactory;
import in.wynk.cache.writer.ShardedRedisCacheWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ShardedRedisCacheManager implements ICacheManager, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(CustomRedisCacheManager.class);
    private final Object monitor = new Object();

    private final RedisCacheConfiguration defaultCacheConfig;
    private final ShardedRedisCacheWriter cacheWriter;

    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);
    private final Map<String, RedisCacheConfiguration> initialCacheConfiguration;

    private volatile Set<String> cacheNames = Collections.emptySet();

    public ShardedRedisCacheManager(IShardedRedisPoolFactory poolFactory, RedisCacheConfiguration defaultCacheConfig) {
        this.cacheWriter = new ShardedRedisCacheWriter(poolFactory);
        this.defaultCacheConfig = defaultCacheConfig;
        this.initialCacheConfiguration = new LinkedHashMap<>();
    }

    public void afterPropertiesSet() {
        this.initializeCaches();
    }

    public void initializeCaches() {
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

    @Nullable
    public Cache getCache(@NonNull String name) {
        Cache cache = this.lookupCache(name);
        if (cache != null) {
            return cache;
        } else {
            Cache missingCache = this.getMissingCache(name);
            if (missingCache != null) {
                synchronized (this.cacheMap) {
                    cache = this.cacheMap.get(name);
                    if (cache == null) {
                        cache = this.decorateCache(missingCache);
                        this.cacheMap.put(name, cache);
                        this.updateCacheNames(name);
                    }
                }
            }
            return cache;
        }
    }

    @Override
    public Cache createCache(String name, ICacheSpec cacheSpec) {
        Cache cache = this.lookupCache(name);
        if (cache != null) {
            return cache;
        } else {
            Cache missingCache = getMissingCache(name, cacheSpec);
            if (missingCache != null) {
                synchronized (monitor) {
                    cache = this.cacheMap.get(name);
                    if (cache == null) {
                        cache = this.decorateCache(missingCache);
                        this.cacheMap.put(name, cache);
                        updateCacheNames(name);
                    }
                }
            }
            return cache;
        }
    }

    @Override
    public boolean containsCache(String key) {
        return this.cacheMap.containsKey(key);
    }

    @NonNull
    public Collection<String> getCacheNames() {
        return this.cacheNames;
    }

    @Nullable
    protected final Cache lookupCache(String name) {
        return this.cacheMap.get(name);
    }

    protected Collection<? extends Cache> loadCaches() {
        List<ShardedRedisCache> caches = new LinkedList<>();

        for (Map.Entry<String, RedisCacheConfiguration> entry : this.initialCacheConfiguration.entrySet()) {
            caches.add(this.createRedisCache(entry.getKey(), entry.getValue()));
        }
        return caches;
    }

    protected Cache getMissingCache(String name) {
        return this.createRedisCache(name, this.defaultCacheConfig);
    }

    protected RedisCache getMissingCache(String name, ICacheSpec cacheSpec) {
        LevelTwoCacheSpec redisCacheSpec = (LevelTwoCacheSpec) cacheSpec.<CompositeCacheSpec>getSpec().getL2CacheSpec();
        return this.createRedisCache(name, this.copyRedisCacheConfig(redisCacheSpec));
    }

    protected Cache decorateCache(Cache cache) {
        return cache;
    }

    protected ShardedRedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
        return new ShardedRedisCache(name, this.cacheWriter, cacheConfig != null ? cacheConfig : this.defaultCacheConfig);
    }

    private void updateCacheNames(String name) {
        Set<String> cacheNames = new LinkedHashSet<>(this.cacheNames.size() + 1);
        cacheNames.addAll(this.cacheNames);
        cacheNames.add(name);
        this.cacheNames = Collections.unmodifiableSet(cacheNames);
    }

    private RedisCacheConfiguration copyRedisCacheConfig(LevelTwoCacheSpec redisCacheSpec) {
        if (redisCacheSpec.getTtl() > 0) {
            RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofSeconds(redisCacheSpec.getTtl()))
                    .serializeKeysWith(this.defaultCacheConfig.getKeySerializationPair())
                    .serializeValuesWith(this.defaultCacheConfig.getValueSerializationPair());
            if (!redisCacheSpec.isNullable())
                redisCacheConfiguration = redisCacheConfiguration.disableCachingNullValues();

            return redisCacheConfiguration;
        }
        return null;
    }
}
