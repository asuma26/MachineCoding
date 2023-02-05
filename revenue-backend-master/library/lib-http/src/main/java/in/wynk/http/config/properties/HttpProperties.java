package in.wynk.http.config.properties;

import in.wynk.http.constant.SocketFactoryType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;


@Getter
@Setter
@ConfigurationProperties("http")
public class HttpProperties {

    private boolean enableTemplateRegistration;
    private Map<String, HttpTemplateProperties> templates;

    @Getter
    @Setter
    public static class HttpTemplateProperties {

        private Pool pool;

        @Getter
        @Setter
        public static class Pool {

            private Connection connection;

            @Getter
            @Setter
            public static class Connection {

                private int max;
                private int timeout;
                private int maxPerHost;
                private int validateAfterInActivity;
                private int closeIdleWaitTime;
                private int defaultKeepAliveTime;
                private Socket socket;
                private Request request;

                @Getter
                @Setter
                public static class Request {
                    private int timeout;
                }

                @Getter
                @Setter
                public static class Socket {
                    private int timeout;
                    private Factory factory;

                    @Getter
                    @Setter
                    public static class Factory {

                        private String type;

                        public SocketFactoryType getType() {
                            return SocketFactoryType.valueOf(type.toUpperCase());
                        }
                    }
                }
            }
        }
    }
}
