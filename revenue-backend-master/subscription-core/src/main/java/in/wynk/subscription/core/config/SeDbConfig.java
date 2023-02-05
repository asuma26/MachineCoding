package in.wynk.subscription.core.config;

import in.wynk.data.config.WynkMongoDbFactoryBuilder;
import in.wynk.data.config.properties.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(mongoTemplateRef = "sedbMongoTemplate", basePackages = {"in.wynk.subscription.core.dao.repository.sedb"})
public class SeDbConfig {

    @Bean
    public MongoDbFactory sedbDbFactory(MongoProperties mongoProperties) {
        return WynkMongoDbFactoryBuilder.buildMongoDbFactory(mongoProperties, "sedb");
    }

    @Bean
    public MongoTemplate sedbMongoTemplate(MongoProperties mongoProperties) {
        return new MongoTemplate(sedbDbFactory(mongoProperties));
    }

}
