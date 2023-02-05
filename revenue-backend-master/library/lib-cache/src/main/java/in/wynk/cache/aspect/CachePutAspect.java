package in.wynk.cache.aspect;

import in.wynk.cache.ICacheManager;
import in.wynk.cache.aspect.advice.CachePut;
import in.wynk.cache.entity.CompositeCacheSpec;
import in.wynk.cache.entity.ICacheSpec;
import in.wynk.cache.entity.LevelOneCacheSpec;
import in.wynk.cache.entity.LevelTwoCacheSpec;
import in.wynk.cache.keygen.KeyGenUtil;
import in.wynk.spel.IRuleEvaluator;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
public class CachePutAspect {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private KeyGenUtil keyGenUtil;

    @Around("execution(@in.wynk.cache.aspect.advice.CachePut * *.*(..))")
    public Object put(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isAllowedCachePut = true;
        Class<?> target = joinPoint.getTarget().getClass();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] keyParams = methodSignature.getParameterNames();
        Object[] valueParams = joinPoint.getArgs();

        CachePut cachePut = method.getAnnotation(CachePut.class);

        Object result = joinPoint.proceed(valueParams);

        if (StringUtils.isNotBlank(cachePut.condition())) {
            if (!evaluateCondition(cachePut.condition(), keyParams, valueParams)) {
                isAllowedCachePut = false;
            }
        }

        if (StringUtils.isNotBlank(cachePut.unless())) {
            if (!evaluateUnlessCondition(cachePut.unless(), result)) {
                isAllowedCachePut = false;
            }
        }

        if (isAllowedCachePut) {
            String key = keyGenUtil.getKey(cachePut.keyGenerator(), cachePut.cacheKey(), joinPoint);
            Cache cache = getCache(cachePut.cacheName(), cachePut);
            cache.put(key, result);
        }

        return result;
    }

    private boolean evaluateCondition(String condition, String[] keys, Object[] values) {
        StandardEvaluationContext root = new StandardEvaluationContext();

        for (int i = 0; i < Objects.requireNonNull(keys).length; i++) {
            root.setVariable(keys[i], values[i]);
        }

        return context.getBean(IRuleEvaluator.class).evaluate(condition, root, Boolean.class);
    }

    private boolean evaluateUnlessCondition(String condition, Object result) {
        StandardEvaluationContext root = new StandardEvaluationContext(result);
        return context.getBean(IRuleEvaluator.class).evaluate(condition, root, Boolean.class);
    }

    private Cache getCache(String key, CachePut cachePut) {
        ICacheManager cacheManager = (ICacheManager) context.getBean(cachePut.cacheManager());
        Cache cache;
        if (cacheManager.containsCache(key)) {
            cache = cacheManager.getCache(key);
        } else {
            cache = cacheManager.createCache(key, getCacheSpec(cachePut));
        }
        return cache;
    }

    private ICacheSpec getCacheSpec(CachePut cachePut) {
        return CompositeCacheSpec.builder()
                .l1CacheSpec(LevelOneCacheSpec.builder()
                        .ttl(cachePut.l1CacheTtl())
                        .nullable(cachePut.l1CacheNullable())
                        .build())
                .l2CacheSpec(LevelTwoCacheSpec.builder()
                        .ttl(cachePut.l2CacheTtl())
                        .nullable(cachePut.l2CacheNullable())
                        .build())
                .build();
    }

}
