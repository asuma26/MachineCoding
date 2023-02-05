package in.wynk.cache.config;

import in.wynk.cache.aspect.CacheEvictAspect;
import in.wynk.cache.aspect.CachePutAspect;
import in.wynk.cache.aspect.CacheablesAspect;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheAspectConfiguration {

    @Bean
    public CacheablesAspect cacheablesAspect() {
        return Aspects.aspectOf(CacheablesAspect.class);
    }

    @Bean
    public CacheEvictAspect cacheEvictAspect() {
        return Aspects.aspectOf(CacheEvictAspect.class);
    }

    @Bean
    public CachePutAspect cachePutAspect() {
        return Aspects.aspectOf(CachePutAspect.class);
    }

}
