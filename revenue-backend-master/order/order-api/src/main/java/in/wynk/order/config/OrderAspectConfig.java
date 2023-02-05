package in.wynk.order.config;

import in.wynk.order.aspect.ManageOrderAspect;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderAspectConfig {

    @Bean
    public ManageOrderAspect manageOrderAspect() {
        return Aspects.aspectOf(ManageOrderAspect.class);
    }


}
