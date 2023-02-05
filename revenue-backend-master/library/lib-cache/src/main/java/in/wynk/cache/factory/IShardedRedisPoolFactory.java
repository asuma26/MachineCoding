package in.wynk.cache.factory;

import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.ShardedJedis;

public interface IShardedRedisPoolFactory extends InitializingBean {

    ShardedJedis getShard();

}
