package in.wynk.cache.writer;

import in.wynk.cache.factory.IShardedRedisPoolFactory;
import in.wynk.logging.BaseLoggingMarkers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.Nullable;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;

import java.time.Duration;

public class ShardedRedisCacheWriter implements RedisCacheWriter {

    private final Logger logger = LoggerFactory.getLogger(ShardedRedisCacheWriter.class);

    private final IShardedRedisPoolFactory factory;

    public ShardedRedisCacheWriter(IShardedRedisPoolFactory factory) {
        this.factory = factory;
    }

    @Override
    public void put(String name, byte[] key, byte[] value, @Nullable Duration ttl) {
        String host = null;
        try (ShardedJedis redis = factory.getShard()) {
            host = redis.getShard(key).getClient().getHost();
            redis.setex(key, (int) ttl.getSeconds(), value).getBytes();
        } catch (Exception e) {
            logger.error(BaseLoggingMarkers.REDIS_ERROR, "unable to set value from redis ip - " + host, e);
            throw e;
        }
    }

    @Override
    public byte[] get(String name, byte[] key) {
        String host = null;
        try (ShardedJedis redis = factory.getShard()) {
            host = redis.getShard(key).getClient().getHost();
            String result = redis.get(new String(key));
            return StringUtils.isEmpty(result) ? null: result.getBytes();
        } catch (Exception e) {
            logger.error(BaseLoggingMarkers.REDIS_ERROR, "unable to get value from redis ip - " + host, e);
            throw e;
        }
    }

    @Override
    public byte[] putIfAbsent(String name, byte[] key, byte[] value, @Nullable Duration ttl) {
        String host = null;
        try (ShardedJedis redis = factory.getShard()) {
            host = redis.getShard(key).getClient().getHost();
            ShardedJedisPipeline pipeline = redis.pipelined();
            pipeline.set(key, value);
            pipeline.pexpire(key, ttl.toMillis());
            pipeline.sync();
            return value;
        } catch (Exception e) {
            logger.error(BaseLoggingMarkers.REDIS_ERROR, "unable to set value from redis ip - " + host, e);
            throw e;
        }
    }

    @Override
    public void remove(String name, byte[] key) {
        String host = null;
        try (ShardedJedis redis = factory.getShard()) {
            host = redis.getShard(key).getClient().getHost();
            Long del = redis.del(key);
        } catch (Exception e) {
            logger.error(BaseLoggingMarkers.REDIS_ERROR, "unable to get value from redis ip - " + host, e);
            throw e;
        }
    }

    @Override
    public void clean(String name, byte[] pattern) {
        String host = null;
        try (ShardedJedis redis = factory.getShard()) {
            String[] key = new String(pattern).split(",");
            host = redis.getShard(key[0]).getClient().getHost();
            ShardedJedisPipeline redisPipeline = redis.pipelined();
            for (String e : key) {
                redisPipeline.del(e);
            }
            redisPipeline.sync();
        } catch (Exception e) {
            logger.error(BaseLoggingMarkers.REDIS_ERROR, "unable to get value from redis ip - " + host, e);
            throw e;
        }
    }
}
