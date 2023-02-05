package in.wynk.ut.base.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import in.wynk.data.config.properties.MongoProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.lang.NonNull;

@Configuration
@EnableReactiveMongoRepositories
public class MongoDbConfig extends AbstractReactiveMongoConfiguration {

    private final MongoProperties mongoProperties;
    private static final String DATA_SOURCE = "filterdb";

    public MongoDbConfig(MongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }

    @Override
    @NonNull
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(mongoProperties.getConnectionSettings(DATA_SOURCE));
    }

    @Override
    @NonNull
    protected String getDatabaseName() {
        return mongoProperties.getSources().get(DATA_SOURCE).getDatabase();
    }
}
