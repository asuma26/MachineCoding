package in.wynk.cache.aspect;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.cache.ICacheManager;
import in.wynk.cache.aspect.advice.Cacheable;
import in.wynk.cache.entity.CompositeCacheSpec;
import in.wynk.cache.entity.ICacheSpec;
import in.wynk.cache.entity.LevelOneCacheSpec;
import in.wynk.cache.entity.LevelTwoCacheSpec;
import in.wynk.cache.keygen.KeyGenUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static in.wynk.cache.constant.CacheConstant.*;
import static in.wynk.logging.BaseLoggingMarkers.CACHE_READ_ERROR;
import static in.wynk.logging.BaseLoggingMarkers.CACHE_WRITE_ERROR;

@Slf4j
@Aspect
public class CacheablesAspect {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private KeyGenUtil keyGenUtil;

    @Around("execution(@in.wynk.cache.aspect.advice.Cacheable * *.*(..))")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        long currentTime = System.currentTimeMillis();
        Class<?> target = joinPoint.getTarget().getClass();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] keyParams = methodSignature.getParameterNames();
        Object[] valueParams = joinPoint.getArgs();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        try {
            String key = keyGenUtil.getKey(cacheable.keyGenerator(), cacheable.cacheKey(), joinPoint);
            Cache cache = getCache(cacheable.cacheName(), cacheable);
            try {
                Cache.ValueWrapper cached = cache.get(key);
                if (cached != null) {
                    AnalyticService.update(method.getName() + CACHE_HIT, true);
                    return cached.get();
                }
            } catch (Exception e) {
                AnalyticService.update(CACHE_FAILURE, true);
                log.error(CACHE_READ_ERROR, e.getMessage(), e);
            }
            AnalyticService.update(method.getName() + CACHE_HIT, false);
            long executionStartTime = System.currentTimeMillis();
            Object object = joinPoint.proceed(valueParams);
            AnalyticService.update(method.getName() + "-executionTIme", System.currentTimeMillis() - executionStartTime);
            if (Objects.nonNull(object)) {
                long writeStartTime = System.currentTimeMillis();
                try {
                    cache.put(key, object);
                } catch (Exception e) {
                    AnalyticService.update(CACHE_FAILURE, true);
                    log.error(CACHE_WRITE_ERROR, e.getMessage(), e);
                }
                AnalyticService.update(method.getName() + "-writeTime", System.currentTimeMillis() - writeStartTime);
            }
            return object;
        } finally {
            AnalyticService.update(method.getName() + TIME, System.currentTimeMillis() - currentTime);
        }
    }

    private Cache getCache(String key, Cacheable cacheable) {
        ICacheManager cacheManager = (ICacheManager) context.getBean(cacheable.cacheManager());
        Cache cache;
        if (cacheManager.containsCache(key)) {
            cache = cacheManager.getCache(key);
        } else {
            cache = cacheManager.createCache(key, getCacheSpec(cacheable));
        }
        return cache;
    }

    private ICacheSpec getCacheSpec(Cacheable cacheable) {
        return CompositeCacheSpec.builder()
                .l1CacheSpec(LevelOneCacheSpec.builder()
                        .ttl(addJitter(cacheable.l1CacheTtl()))
                        .nullable(cacheable.l1CacheNullable())
                        .build())
                .l2CacheSpec(LevelTwoCacheSpec.builder()
                        .ttl(addJitter(cacheable.l2CacheTtl()))
                        .nullable(cacheable.l2CacheNullable())
                        .build())
                .build();
    }

    private long addJitter(long ttl) {
        return ttl + ThreadLocalRandom.current().nextLong(10, 20); // For 2-digit integers, 10-99 inclusive.
    }

}
