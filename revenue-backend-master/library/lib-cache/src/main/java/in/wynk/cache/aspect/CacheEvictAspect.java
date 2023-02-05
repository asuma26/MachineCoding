package in.wynk.cache.aspect;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.cache.ICacheManager;
import in.wynk.cache.aspect.advice.CacheEvict;
import in.wynk.cache.entity.CompositeCacheSpec;
import in.wynk.cache.entity.ICacheSpec;
import in.wynk.cache.entity.LevelOneCacheSpec;
import in.wynk.cache.entity.LevelTwoCacheSpec;
import in.wynk.cache.keygen.KeyGenUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

import static in.wynk.cache.constant.CacheConstant.CACHE_FAILURE;
import static in.wynk.logging.BaseLoggingMarkers.CACHE_ERROR;

@Aspect
public class CacheEvictAspect {

    private static final Logger log = LoggerFactory.getLogger(CacheEvictAspect.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private KeyGenUtil keyGenUtil;

    @Around("execution(@in.wynk.cache.aspect.advice.CacheEvict * *.*(..))")
    public Object evict(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] valueParams = joinPoint.getArgs();

        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

        if (cacheEvict.beforeInvocation()) {
            evict(cacheEvict, joinPoint);
            return joinPoint.proceed(valueParams);
        }

        Object result = joinPoint.proceed(valueParams);
        evict(cacheEvict, joinPoint);
        return result;
    }

    private void evict(CacheEvict cacheEvict, ProceedingJoinPoint joinPoint) {
        try {
            ICacheManager cacheManager = (ICacheManager) context.getBean(cacheEvict.cacheManager());
            Cache cache = cacheManager.containsCache(cacheEvict.cacheName()) ?
                    cacheManager.getCache(cacheEvict.cacheName()) :
                    cacheManager.createCache(cacheEvict.cacheName(), getCacheSpec(cacheEvict));
            String key = keyGenUtil.getKey(cacheEvict.keyGenerator(), cacheEvict.cacheKey(), joinPoint);
            cache.evict(key);
        } catch (Exception e) {
            AnalyticService.update(CACHE_FAILURE, true);
            log.error(CACHE_ERROR, "Failed to evict cache due to {} ", e.getMessage(), e);
        }
    }

    private ICacheSpec getCacheSpec(CacheEvict cacheEvict) {
        return CompositeCacheSpec.builder()
                .l1CacheSpec(LevelOneCacheSpec.builder()
                        .ttl(addJitter(cacheEvict.l1CacheTtl()))
                        .nullable(cacheEvict.l1CacheNullable())
                        .build())
                .l2CacheSpec(LevelTwoCacheSpec.builder()
                        .ttl(addJitter(cacheEvict.l2CacheTtl()))
                        .nullable(cacheEvict.l2CacheNullable())
                        .build())
                .build();
    }

    private long addJitter(long ttl){
        return ttl + ThreadLocalRandom.current().nextLong(10,20);
    }

}
