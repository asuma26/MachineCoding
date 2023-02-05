package in.wynk.cache.cache;

import in.wynk.cache.ICache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class CompositeCache implements ICache {

    private static final Logger logger = LoggerFactory.getLogger(CompositeCache.class);

    private final Cache l1Cache;
    private final Cache l2Cache;

    public CompositeCache(Cache l1Cache, Cache l2Cache) {
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
    }

    @Override
    public String getName() {
        return l1Cache.getName();
    }

    @Override
    public Object getNativeCache() {
        return l1Cache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper wrapper = l1Cache.get(key);
        if (wrapper != null) {
            logger.debug("[L1 CACHE] hit for key: {}", key);
        } else {
            logger.warn("[L1 CACHE] miss for key: {}", key);
            try {
                wrapper = l2Cache.get(key);
            } catch (Throwable th) {
                logger.error("[L2 CACHE] GET failed for key: {} due to ", key, th);
            }
            if (wrapper != null) {
                l1Cache.put(key, wrapper.get());
                logger.debug("[L2 CACHE] hit for key: {}", key);
            } else {
                logger.warn("[L2 CACHE] miss for key: {}", key);
            }
        }
        return wrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> aClass) {
        return (T) get(key);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = (T) get(key);
        if (value == null) {
            try {
                value = valueLoader.call();
            } catch (Throwable th) {
                logger.error("value loader is failed for key: {} due to ", key, th);
            }
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        l1Cache.put(key, value);
        logger.debug("[L1 CACHE] PUT for key: {}", key);
        try {
            l2Cache.put(key, value);
            logger.debug("[L2 CACHE] PUT for key: {}", key);
        } catch (Throwable th) {
            logger.error("[L2 CACHE] PUT is failed for key: {} due to ", key, th);
        }
    }

    @Override
    public void evict(Object key) {
        l1Cache.evict(key);
        logger.debug("[L1 CACHE] EVICTED for key: {}", key);
        try {
            l2Cache.evict(key);
            logger.debug("[L2 CACHE] EVICTED for key: {}", key);
        } catch (Throwable th) {
            logger.error("[L2 CACHE] EVICTED is failed for key: {} due to ", key, th);
        }
    }

    @Override
    public void clear() {
        l1Cache.clear();
        logger.debug("[L1 CACHE] CLEANED");
        try {
            l2Cache.clear();
            logger.debug("[L2 CACHE] CLEANED");
        } catch (Throwable th) {
            logger.error("[L2 CACHE] CLEANED is failed due to ", th);
        }
    }
}
