package in.wynk.targeting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "in.wynk.targeting.repository.mongo.testConfig", mongoTemplateRef = "testConfigMongoTemplate")
public class AdsTestingConfig {
}
