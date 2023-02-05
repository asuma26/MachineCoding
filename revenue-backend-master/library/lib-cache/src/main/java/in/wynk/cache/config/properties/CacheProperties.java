package in.wynk.cache.config.properties;

import in.wynk.cache.constant.CacheConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.connection.RedisPassword;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@Getter
@Setter
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private boolean enabled;
    private String mode;
    private RedisProperties redis;
    private CaffeineProperties caffeine;

    public OperatingMode getCacheMode() {
        return mode != null ? OperatingMode.valueOf(mode.toUpperCase()) : OperatingMode.STANDALONE;
    }

    @Getter
    @Setter
    public static class RedisProperties {

        private boolean usePooling;
        private boolean useSsl;
        private boolean enableJmx;

        private int readTimeout;

        private String name;
        private String mode;
        private String password;
        private String jmxPrefix;


        private CacheProps cache;

        private Connection connection;
        private ClusterProperties cluster;
        private ShardedProperties sharded;
        private StandaloneProperties standalone;

        @Getter
        @Setter
        public static class Connection {

            private int timeout;
            private int maxTotal;
            private int minIdle;
            private int maxIdle;

        }

        @Getter
        @Setter
        public static class ShardedProperties {
            private String password;
            List<HostPortProperties> nodes;

            @Getter
            @Setter
            public static class HostPortProperties {
                private String host;
                private int port;
            }

        }

        @Getter
        @Setter
        public static class ClusterProperties {
            private String nodes;

            public Collection<String> getNodes() {
                return Arrays.asList(nodes.split(CacheConstant.COMMA_DELIMITER));
            }
        }

        @Getter
        @Setter
        public static class StandaloneProperties {
            private String host;
            private int port;
        }

        @Getter
        @Setter
        public static class CacheProps {
            private long ttl;
            private String timeunit;
            private String keyPrefix;
            private boolean usePrefix;
            private boolean cacheNullValues;

            public Duration getTtl() {
                return Duration.of(ttl, ChronoUnit.valueOf(timeunit.toUpperCase()));
            }
        }

        public RedisPassword getPassword() {
            return RedisPassword.of(password);
        }

        public OperatingMode getOperatingMode() {
            return OperatingMode.valueOf(mode.toUpperCase());
        }

    }

    @Getter
    @Setter
    public static class CaffeineProperties {

        private boolean nullable;
        private boolean metrics;
        private EvictionPolicy eviction;

        @Getter
        @Setter
        public static class EvictionPolicy {

            private TimeBasedEviction time;
            private SizeBasedEviction size;
            private ReferenceBasedEviction reference;

            @Getter
            @Setter
            public static class SizeBasedEviction {
                private Integer initial;
                private Long maximum;
            }

            @Getter
            @Setter
            public static class TimeBasedEviction {
                private boolean fixed;
                private Long expireAfterWrite;
                private Long expireAfterAccess;
                private Long expireAfterUpdate;
                private String timeunit;

                public Duration getExpireAfterWrite() {
                    return expireAfterWrite != null ? Duration.of(expireAfterWrite,
                            ChronoUnit.valueOf(timeunit.toUpperCase())) : null;
                }

                public Duration getExpireAfterAccess() {
                    return expireAfterAccess != null ? Duration.of(expireAfterAccess,
                            ChronoUnit.valueOf(timeunit.toUpperCase())) : null;
                }

                public Duration getExpireAfterUpdate() {
                    return expireAfterUpdate != null ? Duration.of(expireAfterUpdate,
                            ChronoUnit.valueOf(timeunit.toUpperCase())) : null;
                }
            }

            @Getter
            @Setter
            public static class ReferenceBasedEviction {
                private boolean weakKeys;
                private boolean weakValues;
                private boolean softValues;
            }

            public TimeBasedEviction getTimeEvictionPolicy() {
                return this.time;
            }

            public SizeBasedEviction getSizeEvictionPolicy() {
                return this.size;
            }

            public ReferenceBasedEviction getReferenceEvictionPolicy() {
                return this.reference;
            }

        }

        public EvictionPolicy getEvictionPolicy() {
            return this.eviction;
        }

    }

}
