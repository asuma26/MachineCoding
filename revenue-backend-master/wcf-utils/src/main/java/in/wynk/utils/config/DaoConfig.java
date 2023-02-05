package in.wynk.utils.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Deprecated
@Configuration
@EnableMongoRepositories(basePackages = "in.wynk.utils.dao.sedb", mongoTemplateRef = "seTemplate")
public class DaoConfig {
}
