package in.wynk.cache.aspect.advice;

import in.wynk.cache.constant.BeanConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

    String cacheKey() default "";

    String cacheName() default "";

    String keyGenerator() default "";

    String cacheManager() default BeanConstant.COMPOSITE_CACHE_MANAGER;

    boolean l1CacheNullable() default false;

    boolean l2CacheNullable() default false;

    /**
     * L1 cache TTL in SECONDS
     */
    long l1CacheTtl() default -1;

    /**
     * L2 cache TTL in SECONDS
     */
    long l2CacheTtl() default -1;

}
