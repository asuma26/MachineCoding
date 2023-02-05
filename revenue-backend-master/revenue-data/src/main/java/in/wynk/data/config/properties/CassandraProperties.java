package in.wynk.data.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties("wynk.cassandra")
public class CassandraProperties {

    private Map<String, CassandraDataSource> sources;
    private boolean enabled;

    public CassandraDataSource get(String dataSource) {
        if (!sources.containsKey(dataSource)) {
            throw new IllegalArgumentException(dataSource + " key does not exists");
        }
        return sources.get(dataSource);
    }

    @Getter
    @Setter
    public static class CassandraDataSource {
        private String contactPoints;
        private int port;
        private String localDC;
        private String keyspace;
        private String username;
        private String password;
        private String consistencyLevel;
        private int fetchSize;
    }


}
