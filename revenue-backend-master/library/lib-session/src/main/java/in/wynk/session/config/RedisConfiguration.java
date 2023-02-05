package in.wynk.session.config;

import in.wynk.session.config.properties.SessionManagerProperties;
import in.wynk.session.config.properties.SessionManagerProperties.RedisProperties;
import in.wynk.session.constant.BeanConstant;
import in.wynk.session.dto.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Objects;

@Configuration
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    public RedisConfiguration(SessionManagerProperties sessionManagerProperties) {
        redisProperties = sessionManagerProperties.getRedis();
    }

    @Bean(BeanConstant.SESSION_REDIS_TEMPLATE)
    public RedisTemplate<String, Session<?>> redisTemplate() {
        RedisTemplate<String, Session<?>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(buildJedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
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
        Objects.requireNonNull(jedisConnectionFactory.getPoolConfig()).setMaxIdle(redisProperties.getConnection().getMaxIdle());
        jedisConnectionFactory.getPoolConfig().setMinIdle(redisProperties.getConnection().getMinIdle());
        jedisConnectionFactory.getPoolConfig().setMaxTotal(redisProperties.getConnection().getMaxTotal());

        if (redisProperties.isEnableJmx()) {
            jedisConnectionFactory.getPoolConfig().setJmxEnabled(redisProperties.isEnableJmx());
            jedisConnectionFactory.getPoolConfig().setJmxNamePrefix(redisProperties.getJmxPrefix());
        }
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

}
