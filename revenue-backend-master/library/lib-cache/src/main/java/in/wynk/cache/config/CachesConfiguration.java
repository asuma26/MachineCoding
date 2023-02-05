package in.wynk.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import in.wynk.cache.config.properties.CacheProperties;
import in.wynk.cache.constant.BeanConstant;
import in.wynk.cache.factory.IShardedRedisPoolFactory;
import in.wynk.cache.keygen.CustomKeyGenerator;
import in.wynk.cache.keygen.SpelBasedKeyGenerator;
import in.wynk.cache.manager.CompositeCacheManager;
import in.wynk.cache.manager.CustomCaffeineCacheManager;
import in.wynk.cache.manager.CustomRedisCacheManager;
import in.wynk.cache.manager.ShardedRedisCacheManager;
import in.wynk.spel.IRuleEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@EnableCaching
@EnableConfigurationProperties({CacheProperties.class})
public class CachesConfiguration implements CachingConfigurer {

    private final CacheProperties cacheProperties;
    private final Caffeine<Object, Object> caffeineSpec;
    private final RedisCacheConfiguration redisCacheConfiguration;

    @Autowired(required = false)
    private IShardedRedisPoolFactory poolFactory;
    @Autowired(required = false)
    private JedisConnectionFactory jedisConnectionFactory;

    public CachesConfiguration(Caffeine<Object, Object> caffeineSpec,
                               CacheProperties cacheProperties,
                               RedisCacheConfiguration redisCacheConfiguration) {
        this.caffeineSpec = caffeineSpec;
        this.cacheProperties = cacheProperties;
        this.redisCacheConfiguration = redisCacheConfiguration;
    }

    @Primary
    @Bean(BeanConstant.COMPOSITE_CACHE_MANAGER)
    public CacheManager cacheManager() {
        if (cacheProperties.isEnabled()) {
            return new CompositeCacheManager(cacheProperties);
        } else {
            return new NoOpCacheManager();
        }
    }

    @Bean(BeanConstant.L1CACHE_MANAGER)
    public CacheManager l1CacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CustomCaffeineCacheManager();
        if (cacheProperties.getCaffeine().isNullable())
            caffeineCacheManager.setAllowNullValues(true);
        caffeineCacheManager.setCaffeine(caffeineSpec);
        return caffeineCacheManager;
    }

    @Bean(BeanConstant.L2CACHE_MANAGER)
    public CacheManager l2CacheManager() {
        if (jedisConnectionFactory != null) {
            RedisCacheManager redisCacheManager = new CustomRedisCacheManager(jedisConnectionFactory, redisCacheConfiguration);
            redisCacheManager.afterPropertiesSet();
            return redisCacheManager;
        } else {
            return new ShardedRedisCacheManager(poolFactory, redisCacheConfiguration);
        }
    }

    @Bean(BeanConstant.CUSTOM_KEY_GENERATOR)
    public KeyGenerator keyGen() {
        return new CustomKeyGenerator();
    }

    @Bean(BeanConstant.SPEL_KEY_GENERATOR)
    public KeyGenerator spelKeyGen(IRuleEvaluator evaluator, KeyGenerator defaultKeyGenerator) {
        return new SpelBasedKeyGenerator(evaluator, defaultKeyGenerator);
    }

    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return keyGen();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }

}
