package in.wynk.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import in.wynk.cache.config.properties.CacheProperties;
import in.wynk.cache.config.properties.CacheProperties.CaffeineProperties;
import in.wynk.cache.config.properties.CacheProperties.CaffeineProperties.EvictionPolicy;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.time.temporal.ChronoUnit;

@Configuration
public class CaffeineCachingConfiguration {

    private final CaffeineProperties caffeineProperties;

    public CaffeineCachingConfiguration(CacheProperties cacheProperties) {
        this.caffeineProperties = cacheProperties.getCaffeine();
    }

    @Bean
    Caffeine<Object, Object> caffeinePops() {
        EvictionPolicy.SizeBasedEviction sizeEviction = caffeineProperties.getEvictionPolicy().getSizeEvictionPolicy();
        EvictionPolicy.TimeBasedEviction timeEviction = caffeineProperties.getEvictionPolicy().getTimeEvictionPolicy();
        EvictionPolicy.ReferenceBasedEviction referenceEviction = caffeineProperties.getEvictionPolicy().getReferenceEvictionPolicy();
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        if(caffeineProperties.isMetrics()) {
            builder.recordStats();
        }
        if (sizeEviction != null)
            builder.initialCapacity(sizeEviction.getInitial())
                    .maximumSize(sizeEviction.getMaximum());
        else
            throw new RuntimeException("Invalid caffeine memory specs");
        if (referenceEviction != null) {
            if (referenceEviction.isWeakKeys())
                builder.weakKeys();
            if (referenceEviction.isWeakValues())
                builder.weakValues();
            else if (referenceEviction.isSoftValues())
                builder.softValues();
        }
        if (timeEviction != null) {
            if (!timeEviction.isFixed()) {
                builder.expireAfter(new Expiry<Key, Object>() {

                    @Override
                    public long expireAfterCreate(@NonNull Key key, @NonNull Object value, long currentTime) {
                        return timeEviction.getExpireAfterWrite().get(ChronoUnit.MILLIS);
                    }

                    @Override
                    public long expireAfterUpdate(@NonNull Key key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
                        return timeEviction.getExpireAfterUpdate().get(ChronoUnit.MILLIS);
                    }

                    @Override
                    public long expireAfterRead(@NonNull Key key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
                        return timeEviction.getExpireAfterAccess().get(ChronoUnit.MILLIS);
                    }
                });
            } else {
                if (timeEviction.getExpireAfterWrite() != null)
                    builder.expireAfterWrite(timeEviction.getExpireAfterWrite());
                if (timeEviction.getExpireAfterAccess() != null)
                    builder.expireAfterAccess(timeEviction.getExpireAfterAccess());
            }
        }
        return builder;
    }

}
