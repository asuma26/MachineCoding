package in.wynk.cache.factory;

import in.wynk.cache.config.properties.CacheProperties.RedisProperties;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

public class ShardedRedisPoolFactory implements IShardedRedisPoolFactory {

    private final RedisProperties redisProperties;
    private ShardedJedisPool pool;

    public ShardedRedisPoolFactory(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Override
    public void afterPropertiesSet() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisProperties.getConnection().getMaxTotal());
        poolConfig.setMaxIdle(redisProperties.getConnection().getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getConnection().getMinIdle());
        if(redisProperties.isEnableJmx()) {
            poolConfig.setJmxEnabled(redisProperties.isEnableJmx());
            poolConfig.setJmxNamePrefix(redisProperties.getJmxPrefix());
        }
        poolConfig.setMaxWaitMillis(redisProperties.getConnection().getTimeout());
        List<JedisShardInfo> shards = new ArrayList<>();
        List<RedisProperties.ShardedProperties.HostPortProperties> nodes = redisProperties.getSharded().getNodes();
        for (RedisProperties.ShardedProperties.HostPortProperties node : nodes) {
            JedisShardInfo si = new JedisShardInfo(node.getHost(), node.getPort(), redisProperties.getConnection().getTimeout());
            if (StringUtils.isNotBlank(redisProperties.getSharded().getPassword()))
                si.setPassword(redisProperties.getSharded().getPassword());
            shards.add(si);
        }
        pool = new ShardedJedisPool(poolConfig, shards);
    }

    @Override
    public ShardedJedis getShard() {
        return pool.getResource();
    }

}
