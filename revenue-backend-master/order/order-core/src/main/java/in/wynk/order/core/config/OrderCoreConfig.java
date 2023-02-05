package in.wynk.order.core.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EntityScan(basePackages = "in.wynk.order.core.dao.entity")
@EnableJpaRepositories(basePackages = "in.wynk.order.core.dao.repository")
public class OrderCoreConfig {
}
