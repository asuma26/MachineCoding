package in.wynk.utils.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "in.wynk.targeting.core.dao.repository.mongo", mongoTemplateRef = "adsTemplate")
public class AdsDbConfig {
}
