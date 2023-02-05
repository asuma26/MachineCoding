package in.wynk.cache.cache;

import in.wynk.cache.writer.ShardedRedisCacheWriter;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

public class ShardedRedisCache extends RedisCache {

    public ShardedRedisCache(String name, ShardedRedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
    }

}
