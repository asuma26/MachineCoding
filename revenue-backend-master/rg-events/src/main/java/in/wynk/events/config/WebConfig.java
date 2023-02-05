package in.wynk.events.config;

import in.wynk.events.config.properties.EventProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final EventProperties eventProperties;

    public WebConfig(EventProperties eventProperties) {
        this.eventProperties = eventProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(eventProperties.getCors().getAllowed().getOrigins())
                .allowedMethods(eventProperties.getCors().getAllowed().getMethods())
                .maxAge(eventProperties.getCors().getMaxAge());
    }
}
