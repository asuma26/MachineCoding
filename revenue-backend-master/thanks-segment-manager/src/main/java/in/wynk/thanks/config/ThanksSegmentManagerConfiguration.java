package in.wynk.thanks.config;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.core.policies.LoggingRetryPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.wynk.auth.constant.BeanConstant;
import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.mapper.S2SPreAuthTokenMapper;
import in.wynk.auth.provider.S2SAuthenticationProvider;
import in.wynk.auth.service.IClientDetailsService;
import in.wynk.auth.service.IS2SDetailsService;
import in.wynk.auth.service.impl.S2SClientDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThanksSegmentManagerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ThanksSegmentManagerConfiguration.class);
    @Value("${cassandra.contactpoints}")
    private String cassandraIPs;
    @Value("${cassandra.port}")
    private int cassandraPort;
    @Value("${cassandra.thanks.keyspace}")
    private String cassandraKeySpace;
    @Value("${cassandra.username}")
    private String cassandraUsername;
    @Value("${cassandra.password}")
    private String cassandraPassword;
    @Value("${cassandra.local.dc:datacenter1}")
    private String localDC;

    @Bean
    public Gson gson(){
        return new GsonBuilder().disableHtmlEscaping().create();
    }

    @Bean
    public IS2SDetailsService s2sDetailsService(IClientDetailsService<Client> thanksClientDetailsService) {
        return new S2SClientDetailsService(thanksClientDetailsService);
    }

    @Bean
    public AuthenticationProvider s2sAuthenticationProvider(IS2SDetailsService s2sDetailsService) {
        return new S2SAuthenticationProvider(s2sDetailsService);
    }

    @Bean(BeanConstant.PRE_AUTH_S2S_DETAILS_TOKEN_MAPPER)
    public S2SPreAuthTokenMapper preAuthS2STokenMapper() {
        return new S2SPreAuthTokenMapper();
    }

    @Bean
    @Deprecated
    public CassandraOperations cassandraOperations() {
        logger.info("env {}, cassandraIps: {}", System.getProperty("env"), cassandraIPs);
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setConnectionsPerHost(HostDistance.LOCAL, 2, 8);
        poolingOptions.setConnectionsPerHost(HostDistance.REMOTE, 2, 8);

        SocketOptions socketOptions = new SocketOptions();
        socketOptions.setConnectTimeoutMillis(5000);
        socketOptions.setKeepAlive(true);
        socketOptions.setReuseAddress(true);
        socketOptions.setSoLinger(60);
        socketOptions.setTcpNoDelay(true);
        socketOptions.setReadTimeoutMillis(5000);
        String[] ips = cassandraIPs.split(",");
        String cassandra_user = System.getenv("cassandra_user");
        String cassandra_password = System.getenv("cassandra_password");
        if (StringUtils.isNotBlank(cassandra_user) && StringUtils.isNotBlank(cassandra_password)) {
            cassandraUsername = cassandra_user;
            cassandraPassword = cassandra_password;
        }
        Cluster cluster = Cluster.builder().withoutJMXReporting().addContactPoints(ips)
                .withSocketOptions(socketOptions).withLoadBalancingPolicy(DCAwareRoundRobinPolicy.builder().withLocalDc(localDC).build())
                .withPoolingOptions(poolingOptions).withRetryPolicy(new LoggingRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE))
                .withReconnectionPolicy(new ConstantReconnectionPolicy(10000)).withPort(cassandraPort).withCredentials(cassandraUsername, cassandraPassword).build();
        Session session = cluster.connect(cassandraKeySpace);

        return new CassandraTemplate(session);
    }

    @Bean
    public ExecutorService executorService(){
        return Executors.newFixedThreadPool(4);
    }

}
