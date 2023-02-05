package in.wynk.cache;

import in.wynk.cache.entity.ICacheSpec;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public interface ICacheManager extends CacheManager {

    Cache createCache(String key, ICacheSpec cacheSpec);

    boolean containsCache(String key);

}
