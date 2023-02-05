package in.wynk.session.config.properties;

import in.wynk.session.constant.OperatingMode;
import in.wynk.session.constant.SessionConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.connection.RedisPassword;

import java.util.Arrays;
import java.util.Collection;

@Getter
@Setter
@ConfigurationProperties("session")
public class SessionManagerProperties {

    private RedisProperties redis;

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

        private Connection connection;
        private ClusterProperties cluster;
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
        public static class ClusterProperties {
            private String nodes;

            public Collection<String> getNodes() {
                return Arrays.asList(nodes.split(SessionConstant.COMMA_DELIMITER));
            }
        }

        @Getter
        @Setter
        public static class StandaloneProperties {
            private String host;
            private int port;
        }

        public RedisPassword getPassword() {
            return RedisPassword.of(password);
        }

        public OperatingMode getOperatingMode() {
            return OperatingMode.valueOf(mode.toUpperCase());
        }

    }
}
