package in.wynk.coupon.core.config;

import in.wynk.coupon.core.constant.BeanConstant;
import in.wynk.data.config.WynkMongoDbFactoryBuilder;
import in.wynk.data.config.properties.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "in.wynk.coupon.core.dao", mongoTemplateRef = BeanConstant.COUPON_MONGO_TEMPLATE_REF)
public class CouponCoreConfig {

    public MongoDbFactory couponDbFactory(MongoProperties mongoProperties) {
        return WynkMongoDbFactoryBuilder.buildMongoDbFactory(mongoProperties, "coupons");
    }

    @Bean(BeanConstant.COUPON_MONGO_TEMPLATE_REF)
    public MongoTemplate couponMongoTemplate(MongoProperties mongoProperties) {
        return new MongoTemplate(couponDbFactory(mongoProperties));
    }

}
