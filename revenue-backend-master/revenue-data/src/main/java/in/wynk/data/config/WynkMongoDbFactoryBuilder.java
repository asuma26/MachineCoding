package in.wynk.data.config;

import in.wynk.data.config.properties.MongoProperties;
import org.bson.BsonUndefined;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnProperty("wynk.mongodb.enabled")
public class WynkMongoDbFactoryBuilder {

    public static MongoDbFactory buildMongoDbFactory(MongoProperties mongoProperties, String dataSource) {
        return new SimpleMongoClientDbFactory(mongoProperties.getConnectionSettings(dataSource));
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(BsonUndefinedToNullObjectConverterFactory.INSTANCE);
        return new MongoCustomConversions(converters);
    }

    @ReadingConverter
    enum BsonUndefinedToNullObjectConverterFactory implements Converter<BsonUndefined, String> {
        INSTANCE;

        @Override
        public String convert(@Nullable BsonUndefined source) {
            return null;
        }
    }

}