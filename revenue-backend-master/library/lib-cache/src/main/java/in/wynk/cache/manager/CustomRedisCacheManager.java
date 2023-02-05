package in.wynk.cache.manager;

import in.wynk.cache.ICacheManager;
import in.wynk.cache.entity.CompositeCacheSpec;
import in.wynk.cache.entity.ICacheSpec;
import in.wynk.cache.entity.LevelTwoCacheSpec;
import in.wynk.cache.writer.CustomRedisCacheWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomRedisCacheManager extends RedisCacheManager implements ICacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CustomRedisCacheManager.class);

    private final Object monitor = new Object();
    private final RedisCacheConfiguration defaultCacheConfig;
    private final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

    private volatile Set<String> cacheNames = Collections.emptySet();

    public CustomRedisCacheManager(JedisConnectionFactory jedisConnectionFactory, RedisCacheConfiguration defaultCacheConfiguration) {
        super(new CustomRedisCacheWriter(jedisConnectionFactory), defaultCacheConfiguration);
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    public CustomRedisCacheManager(JedisConnectionFactory jedisConnectionFactory, RedisCacheConfiguration defaultCacheConfiguration, String... initialCacheNames) {
        super(new CustomRedisCacheWriter(jedisConnectionFactory), defaultCacheConfiguration, initialCacheNames);
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    public CustomRedisCacheManager(JedisConnectionFactory jedisConnectionFactory, RedisCacheConfiguration defaultCacheConfiguration, boolean allowInFlightCacheCreation, String... initialCacheNames) {
        super(new CustomRedisCacheWriter(jedisConnectionFactory), defaultCacheConfiguration, allowInFlightCacheCreation, initialCacheNames);
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    public CustomRedisCacheManager(JedisConnectionFactory jedisConnectionFactory, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
        super(new CustomRedisCacheWriter(jedisConnectionFactory), defaultCacheConfiguration, initialCacheConfigurations);
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    public CustomRedisCacheManager(JedisConnectionFactory jedisConnectionFactory, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(new CustomRedisCacheWriter(jedisConnectionFactory), defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
        this.defaultCacheConfig = defaultCacheConfiguration;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableCollection(Stream.concat(cacheNames.stream(), super.getCacheNames().stream()).collect(Collectors.toList()));
    }

    @Override
    public Cache createCache(String key, ICacheSpec cacheSpec) {
        Cache cache = this.cacheMap.get(key);
        if (cache != null) {
            return cache;
        } else {
            Cache missingCache = getMissingCache(key, cacheSpec);
            if (missingCache != null) {
                synchronized (monitor) {
                    cache = this.cacheMap.get(key);
                    if (cache == null) {
                        cache = this.decorateCache(missingCache);
                        this.cacheMap.put(key, cache);
                        updateCacheNames(key);
                    }
                }
            }
            return cache;
        }
    }

    @Override
    public Cache getCache(@NonNull String name) {
        return this.cacheMap.get(name);
    }

    @Override
    public boolean containsCache(@NonNull String key) {
        return this.cacheMap.containsKey(key);
    }

    protected RedisCache getMissingCache(String name, ICacheSpec cacheSpec) {
        LevelTwoCacheSpec redisCacheSpec = (LevelTwoCacheSpec) cacheSpec.<CompositeCacheSpec>getSpec().getL2CacheSpec();
        if (redisCacheSpec.getTtl() > 0) {
            RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofSeconds(redisCacheSpec.getTtl()));
            if (!redisCacheSpec.isNullable())
                redisCacheConfiguration.disableCachingNullValues();
            return this.createRedisCache(name, redisCacheConfiguration);
        } else {
            return super.createRedisCache(name, defaultCacheConfig);
        }
    }

    private void updateCacheNames(String name) {
        Set<String> cacheNames = new LinkedHashSet(this.cacheNames.size() + 1);
        cacheNames.addAll(this.cacheNames);
        cacheNames.add(name);
        this.cacheNames = Collections.unmodifiableSet(cacheNames);
    }
}
