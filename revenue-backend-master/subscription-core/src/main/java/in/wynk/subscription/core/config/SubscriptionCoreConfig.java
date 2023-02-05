package in.wynk.subscription.core.config;

import in.wynk.data.config.WynkMongoDbFactoryBuilder;
import in.wynk.data.config.properties.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableMongoRepositories(mongoTemplateRef = "subscriptionMongoTemplate", basePackages = {"in.wynk.subscription.core.dao.repository.subscription"})
public class SubscriptionCoreConfig {

    @Bean(name = {"subscriptionDbFactory", "mongoDbFactory"})
    @Primary
    public MongoDbFactory subscriptionDbFactory(MongoProperties mongoProperties) {
        return WynkMongoDbFactoryBuilder.buildMongoDbFactory(mongoProperties, "subscription");
    }

    @Bean(name = {"subscriptionMongoTemplate", "mongoTemplate"})
    @Primary
    public MongoTemplate subscriptionMongoTemplate(MongoDbFactory subscriptionDbFactory) {
        return new MongoTemplate(subscriptionDbFactory);
    }

    public MongoDbFactory filterUsersDbFactory(MongoProperties mongoProperties) {
        return WynkMongoDbFactoryBuilder.buildMongoDbFactory(mongoProperties, "filterUsers");
    }

    @Bean(name = "filterUsersMongoTemplate")
    public MongoTemplate filterUsersMongoTemplate(MongoProperties mongoProperties) {
        return new MongoTemplate(filterUsersDbFactory(mongoProperties));
    }
}
