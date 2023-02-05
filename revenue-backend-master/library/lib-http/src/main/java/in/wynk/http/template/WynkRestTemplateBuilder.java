package in.wynk.http.template;

import in.wynk.http.config.properties.HttpProperties.HttpTemplateProperties;
import in.wynk.http.constant.ConnectionType;
import in.wynk.http.constant.HttpConstant;
import in.wynk.http.constant.HttpMarker;
import in.wynk.http.constant.SocketFactoryType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
public class WynkRestTemplateBuilder {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private HttpTemplateProperties httpTemplateProperties;
        private List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors;

        public RestTemplate build() {
            HttpTemplateProperties.Pool.Connection connection = httpTemplateProperties.getPool().getConnection();
            PoolingHttpClientConnectionManager poolingManager = poolingHttpClientConnectionManager(connection);
            ConnectionKeepAliveStrategy connectionKeepAliveStrategy = connectionKeepAliveStrategy(connection);
            CloseableHttpClient closeableHttpClient = closeableHttpClient(connection, poolingManager, connectionKeepAliveStrategy);
            ClientHttpRequestFactory clientHttpRequestFactory = clientHttpRequestFactory(closeableHttpClient);
            return restTemplate(clientHttpRequestFactory);
        }

        public Builder withInterceptor(List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors) {
            this.clientHttpRequestInterceptors = clientHttpRequestInterceptors;
            return this;
        }

        public Builder withHttpTemplateProperties(HttpTemplateProperties httpTemplateProperties) {
            this.httpTemplateProperties = httpTemplateProperties;
            return this;
        }

        private RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
            RestTemplate template = new RestTemplate(clientHttpRequestFactory);
            if (clientHttpRequestInterceptors != null && clientHttpRequestInterceptors.size() > 0)
                template.setInterceptors(clientHttpRequestInterceptors);
            return template;
        }

        private ClientHttpRequestFactory clientHttpRequestFactory(CloseableHttpClient closeableHttpClient) {
            HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            clientHttpRequestFactory.setHttpClient(closeableHttpClient);
            ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(clientHttpRequestFactory);
            return factory;
        }

        private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(HttpTemplateProperties.Pool.Connection connection) {
            RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistryBuilder = RegistryBuilder.create();

            if (connection.getSocket().getFactory().getType() == SocketFactoryType.BOTH ||
                    connection.getSocket().getFactory().getType() == SocketFactoryType.SSL) {

                SSLContextBuilder builder = new SSLContextBuilder();

                try {
                    builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                    SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build());
                    socketFactoryRegistryBuilder.register(ConnectionType.HTTPS.getType(), sslConnectionSocketFactory);
                } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                    log.error(HttpMarker.REST_TEMPLATE_REGISTRATION, "Pooling connection manager initialisation is failed due to {} ", e.getMessage());
                }

            }

            if (connection.getSocket().getFactory().getType() == SocketFactoryType.BOTH ||
                    connection.getSocket().getFactory().getType() == SocketFactoryType.PLAIN) {
                socketFactoryRegistryBuilder.register(ConnectionType.HTTP.getType(), new PlainConnectionSocketFactory());
            }

            PoolingHttpClientConnectionManager poolingManager = new PoolingHttpClientConnectionManager(socketFactoryRegistryBuilder.build());
            poolingManager.setMaxTotal(connection.getMax());
            poolingManager.setDefaultMaxPerRoute(connection.getMaxPerHost());
            poolingManager.setValidateAfterInactivity(connection.getValidateAfterInActivity());
            return poolingManager;
        }

        private ConnectionKeepAliveStrategy connectionKeepAliveStrategy(HttpTemplateProperties.Pool.Connection connection) {
            return new KeepConnectionAliveStrategy(connection);
        }

        private CloseableHttpClient closeableHttpClient(HttpTemplateProperties.Pool.Connection connection, PoolingHttpClientConnectionManager poolingManager, ConnectionKeepAliveStrategy keepAliveStrategy) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connection.getRequest().getTimeout())
                    .setConnectTimeout(connection.getTimeout())
                    .setSocketTimeout(connection.getSocket().getTimeout()).build();

            return HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .setConnectionManager(poolingManager)
                    .setKeepAliveStrategy(keepAliveStrategy)
                    .build();
        }

        private static final class KeepConnectionAliveStrategy implements ConnectionKeepAliveStrategy {

            private final HttpTemplateProperties.Pool.Connection connection;

            KeepConnectionAliveStrategy(HttpTemplateProperties.Pool.Connection connection) {
                this.connection = connection;
            }

            @Override
            public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                HeaderElementIterator itr = new BasicHeaderElementIterator((httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE)));
                while (itr.hasNext()) {
                    HeaderElement header = itr.nextElement();
                    String param = header.getName();
                    String value = header.getValue();
                    if (value != null && param.equalsIgnoreCase(HttpConstant.TIMEOUT_kEY)) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return connection.getDefaultKeepAliveTime();
            }
        }
    }

}
