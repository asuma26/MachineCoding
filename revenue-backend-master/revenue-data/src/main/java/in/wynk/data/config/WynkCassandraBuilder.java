package in.wynk.data.config;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.*;
import in.wynk.data.config.properties.CassandraProperties;
import in.wynk.data.config.properties.CassandraProperties.CassandraDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;

import static org.springframework.data.cassandra.config.CassandraClusterFactoryBean.DEFAULT_PORT;

@Configuration
@EnableConfigurationProperties({CassandraProperties.class})
@ConditionalOnProperty("wynk.cassandra.enabled")
public class WynkCassandraBuilder {

    public static CassandraOperations cassandraOperations(CassandraDataSource cassandraDataSource) {
        String[] ips = cassandraDataSource.getContactPoints().split(",");
        int port = cassandraDataSource.getPort() > 0 ? cassandraDataSource.getPort() : DEFAULT_PORT;
        Cluster cluster = Cluster.builder().withoutJMXReporting().addContactPoints(ips).withPort(port)
                .withSocketOptions(getSocketOptions()).withLoadBalancingPolicy(getLoadBalancingPolicy(cassandraDataSource.getLocalDC()))
                .withPoolingOptions(getPoolingOptions()).withRetryPolicy(getRetryPolicy())
                .withReconnectionPolicy(getReconnectionPolicy()).withAuthProvider(authProvider(cassandraDataSource))
                .withQueryOptions(getQueryOptions(cassandraDataSource)).build();
        Session session = cluster.connect(cassandraDataSource.getKeyspace());
        return new CassandraTemplate(session);
    }

    private static AuthProvider authProvider(CassandraDataSource cassandraDataSource) {
        if (StringUtils.isAnyBlank(cassandraDataSource.getUsername(), cassandraDataSource.getPassword())) {
            return AuthProvider.NONE;
        }
        return new PlainTextAuthProvider(cassandraDataSource.getUsername(), cassandraDataSource.getPassword());
    }

    private static QueryOptions getQueryOptions(CassandraDataSource dataSource) {
        QueryOptions queryOptions = new QueryOptions();
        ConsistencyLevel consistencyLevel = ConsistencyLevel.LOCAL_QUORUM;
        if (StringUtils.isNotBlank(dataSource.getConsistencyLevel())) {
            consistencyLevel = ConsistencyLevel.valueOf(dataSource.getConsistencyLevel());
        }
        queryOptions.setConsistencyLevel(consistencyLevel);
        if (dataSource.getFetchSize() > 0) {
            queryOptions.setFetchSize(dataSource.getFetchSize());
        } else {
            queryOptions.setFetchSize(200);
        }
        return queryOptions;
    }


    private static SocketOptions getSocketOptions() {
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.setConnectTimeoutMillis(1000);
        socketOptions.setKeepAlive(true);
        socketOptions.setReuseAddress(true);
        socketOptions.setSoLinger(60);
        socketOptions.setTcpNoDelay(true);
        socketOptions.setReadTimeoutMillis(3000);
        return socketOptions;
    }

    private static PoolingOptions getPoolingOptions() {
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setConnectionsPerHost(HostDistance.LOCAL, 2, 8);
        poolingOptions.setConnectionsPerHost(HostDistance.REMOTE, 2, 8);
        poolingOptions.setPoolTimeoutMillis(1000);
        poolingOptions.setMaxQueueSize(10);
        return poolingOptions;
    }

    private static LoadBalancingPolicy getLoadBalancingPolicy(String localDc) {
        return DCAwareRoundRobinPolicy.builder().withLocalDc(localDc).build();
    }

    private static RetryPolicy getRetryPolicy() {
        return new LoggingRetryPolicy(FallthroughRetryPolicy.INSTANCE);
    }

    private static ReconnectionPolicy getReconnectionPolicy() {
        return new ConstantReconnectionPolicy(5000);
    }
}