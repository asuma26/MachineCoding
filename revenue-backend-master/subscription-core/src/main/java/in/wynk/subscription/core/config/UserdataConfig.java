package in.wynk.subscription.core.config;

import in.wynk.data.config.WynkCassandraBuilder;
import in.wynk.data.config.properties.CassandraProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;


/**
 * @author Abhishek
 * @created 19/06/20
 */
@Configuration
@EnableCassandraRepositories(cassandraTemplateRef = "userdata", basePackages = {"in.wynk.subscription.core.dao.repository.userdata"})
public class UserdataConfig {

    @Bean
    public CassandraOperations userdata(CassandraProperties cassandraProperties){
        return WynkCassandraBuilder.cassandraOperations(cassandraProperties.get("userdata"));
    }
}
