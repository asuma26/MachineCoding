package in.wynk.utils.config;

import in.wynk.data.config.properties.MongoProperties;
import org.bson.BsonUndefined;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

@Configuration
@EnableConfigurationProperties({MongoProperties.class})
public class MongoConfig {

    private final MongoProperties mongoProperties;

    public MongoConfig(MongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }

    @Bean
    public MongoDbFactory adsDbFactory() {
        return new SimpleMongoClientDbFactory(mongoProperties.getConnectionSettings("ads"));
    }

    @Bean
    public MongoDbFactory seDbFactory() {
        return new SimpleMongoClientDbFactory(mongoProperties.getConnectionSettings("se"));
    }

    @Bean
    public MongoDbFactory paymentsDbFactory() {
        return new SimpleMongoClientDbFactory(mongoProperties.getConnectionSettings("payment"));
    }

    @Bean
    public MongoDbFactory testingConfigDbFactory() {
        return new SimpleMongoClientDbFactory(mongoProperties.getConnectionSettings("testingConfig"));
    }

    @Bean
    public MongoTemplate adsTemplate() {
        return new MongoTemplate(adsDbFactory());
    }

    @Bean
    public MongoTemplate seTemplate() {
        return new MongoTemplate(seDbFactory());
    }

    @Bean
    public MongoTemplate paymentsTemplate() {
        return new MongoTemplate(paymentsDbFactory());
    }

    @Bean
    public MongoTemplate testingConfigTemplate() {
        return new MongoTemplate(testingConfigDbFactory());
    }

    @ReadingConverter
    enum BsonUndefinedToNullObjectConverterFactory implements Converter<BsonUndefined, String> {

        INSTANCE;

        @Override
        public String convert(BsonUndefined source) {
            return null;
        }

    }

}
