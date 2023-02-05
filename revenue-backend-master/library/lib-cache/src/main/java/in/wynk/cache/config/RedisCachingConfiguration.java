package in.wynk.cache.config;

import in.wynk.cache.config.properties.CacheProperties;
import in.wynk.cache.config.properties.CacheProperties.RedisProperties;
import in.wynk.cache.factory.IShardedRedisPoolFactory;
import in.wynk.cache.factory.ShardedRedisPoolFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
public class RedisCachingConfiguration {

    private final RedisProperties redisProperties;

    public RedisCachingConfiguration(CacheProperties cacheProperties) {
        this.redisProperties = cacheProperties.getRedis();
    }

    @Bean
    @ConditionalOnExpression("!'${cache.redis.mode}'.equals('sharded')")
    public JedisConnectionFactory redisConnectionFactory() {
        return buildJedisConnectionFactory();
    }

    @Bean
    @ConditionalOnExpression("'${cache.redis.mode}'.equals('sharded')")
    public IShardedRedisPoolFactory shardedRedisPoolFactory() {
        return new ShardedRedisPoolFactory(redisProperties);
    }

    private JedisConnectionFactory buildJedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory;
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = JedisClientConfiguration.builder()
                .clientName(redisProperties.getName())
                .connectTimeout(Duration.ofMillis(redisProperties.getConnection().getTimeout()))
                .readTimeout(Duration.ofMillis(redisProperties.getReadTimeout()));

        if (redisProperties.isUseSsl()) {
            builder.useSsl();
        }

        if (redisProperties.isUsePooling()) {
            builder.usePooling();
        }

        switch (redisProperties.getOperatingMode()) {
            case STANDALONE:
                RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
                standaloneConfiguration.setHostName(redisProperties.getStandalone().getHost());
                standaloneConfiguration.setPort(redisProperties.getStandalone().getPort());
                standaloneConfiguration.setPassword(redisProperties.getPassword());
                jedisConnectionFactory = new JedisConnectionFactory(standaloneConfiguration, builder.build());
                break;
            case CLUSTER:
                RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
                clusterConfiguration.setPassword(redisProperties.getPassword());
                jedisConnectionFactory = new JedisConnectionFactory(clusterConfiguration, builder.build());
                break;
            default:
                throw new RuntimeException("Invalid Redis Operating Mode");
        }
        jedisConnectionFactory.getPoolConfig().setMaxIdle(redisProperties.getConnection().getMaxIdle());
        jedisConnectionFactory.getPoolConfig().setMinIdle(redisProperties.getConnection().getMinIdle());
        jedisConnectionFactory.getPoolConfig().setMaxTotal(redisProperties.getConnection().getMaxTotal());

        if (redisProperties.isEnableJmx()) {
            jedisConnectionFactory.getPoolConfig().setJmxEnabled(redisProperties.isEnableJmx());
            jedisConnectionFactory.getPoolConfig().setJmxNamePrefix(redisProperties.getJmxPrefix());
        }
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        RedisProperties.CacheProps cacheProps = redisProperties.getCache();
        org.springframework.data.redis.cache.RedisCacheConfiguration redisCacheConfiguration = org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.entryTtl(cacheProps.getTtl())
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(RedisSerializer.json()));

        if (cacheProps.isUsePrefix()) {
            redisCacheConfiguration = redisCacheConfiguration.prefixKeysWith(cacheProps.getKeyPrefix());
        } else {
            redisCacheConfiguration = redisCacheConfiguration.disableKeyPrefix();
        }
        if (!cacheProps.isCacheNullValues()) {
            redisCacheConfiguration = redisCacheConfiguration.disableCachingNullValues();
        }
        return redisCacheConfiguration;
    }


}
