package in.wynk.client.core.config;

import in.wynk.client.core.constant.BeanConstant;
import in.wynk.data.config.WynkMongoDbFactoryBuilder;
import in.wynk.data.config.properties.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "in.wynk.client.core.dao", mongoTemplateRef = BeanConstant.CLIENT_DETAILS_MONGO_TEMPLATE_REF)
public class ClientCoreConfig {

    @Bean(BeanConstant.CLIENT_DETAILS_MONGO_DB_FACTORY)
    public MongoDbFactory clientMongoDbFactory(MongoProperties mongoProperties) {
        return WynkMongoDbFactoryBuilder.buildMongoDbFactory(mongoProperties, "client");
    }

    @Bean(BeanConstant.CLIENT_DETAILS_MONGO_TEMPLATE_REF)
    public MongoTemplate clientDetailsMongoTemplate(MongoProperties mongoProperties) {
        return new MongoTemplate(clientMongoDbFactory(mongoProperties));
    }

}
