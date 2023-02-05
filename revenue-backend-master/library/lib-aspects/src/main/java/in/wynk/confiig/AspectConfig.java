package in.wynk.confiig;

import in.wynk.aspect.AnalyticAspect;
import in.wynk.aspect.TimeItAspect;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AspectConfig {

    @Bean
    public TimeItAspect timeItAspect() {
        return Aspects.aspectOf(TimeItAspect.class);
    }

    @Bean
    public AnalyticAspect analyticAspect() {
        return Aspects.aspectOf(AnalyticAspect.class);
    }

}
