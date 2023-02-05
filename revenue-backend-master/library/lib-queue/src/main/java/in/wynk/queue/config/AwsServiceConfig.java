package in.wynk.queue.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import in.wynk.queue.config.properties.AmazonSdkProperties;
import in.wynk.queue.constant.BeanConstant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {AmazonSdkProperties.class})
public class AwsServiceConfig {

    private final AmazonSdkProperties sdkProperties;

    public AwsServiceConfig(AmazonSdkProperties sdkProperties) {
        this.sdkProperties = sdkProperties;
    }


    @Bean(BeanConstant.SQS_MANAGER)
    public AmazonSQS createSqsClient() {
        return AmazonSQSClientBuilder.standard()
                .withRegion(sdkProperties.getSdk().getRegions())
                .build();
    }

}
