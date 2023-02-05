package in.wynk.utils.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableConfigurationProperties
@EnableMongoRepositories(basePackages = "in.wynk.payment.core.dao.repository", mongoTemplateRef = "paymentsTemplate")
public class PaymentsConfig {
}
