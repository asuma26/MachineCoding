package in.wynk.events.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.wynk.events.config.properties.EventProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({EventProperties.class})
public class EventsConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }
}
