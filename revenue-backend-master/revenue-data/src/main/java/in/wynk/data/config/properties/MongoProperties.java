package in.wynk.data.config.properties;

import com.mongodb.ConnectionString;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties("wynk.mongodb")
public class MongoProperties {

    private int minConnectionsPerHost;
    private int maxConnectionsPerHost;
    private int threadsAllowedToBlockForConnectionMultiplier;
    private int maxWaitTimeMS;
//    private int maxLifeTimeMs;
    private int connectTimeout;
    private int serverSelectionTimeoutMs;
    private int socketTimeout;
    private int maxIdleTimeMs;
    private String readPreference;
    private String replicaSet;
    private boolean enabled;
    private Map<String, MongoDataSource> sources;

    public ConnectionString getConnectionSettings(String dataSource) {
        if (!sources.containsKey(dataSource)) {
            throw new IllegalArgumentException(dataSource + " key does not exists");
        }
        String connectionString = this.sources.get(dataSource).getConnectionSettings();
        StringBuilder builder = new StringBuilder(connectionString);
        builder.append("?");
        builder.append("&connectTimeoutMS=").append(connectTimeout);
        builder.append("&socketTimeoutMS=").append(socketTimeout);
        builder.append("&minPoolSize=").append(minConnectionsPerHost);
        builder.append("&maxPoolSize=").append(maxConnectionsPerHost);
        builder.append("&serverselectiontimeoutms=").append(serverSelectionTimeoutMs);
        builder.append("&waitqueuemultiple=").append(threadsAllowedToBlockForConnectionMultiplier);
        builder.append("&waitqueuetimeoutms=").append(maxWaitTimeMS);
//        builder.append("&maxLifeTimeMS=").append(maxLifeTimeMs);
//        builder.append("&maxidletimems=").append(maxIdleTimeMs);
        builder.append("&readpreference=").append(readPreference);
//        builder.append("&replicaSet=").append(replicaSet);
        return new ConnectionString(builder.toString());
    }

    @Getter
    @Setter
    public static class MongoDataSource {
        private String nodes;
        private String username;
        private String password;
        private String database;

        private String getConnectionSettings() {
            StringBuilder builder = new StringBuilder("mongodb://");
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                builder.append(username);
                builder.append(":");
                builder.append(password);
                builder.append("@");
            }
            builder.append(nodes);
            builder.append("/");
            builder.append(database);
            return builder.toString();
        }
    }

}

