package in.wynk.vas.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author Abhishek
 * @created 19/06/20
 */
@Configuration
@Profile("!prod")
public class VasClientConfig {

    @Bean(name = "vasRestTemplate")
    public RestTemplate vasRestTemplate(){
        //TODO: use HTTP factory for our template.
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new VasHttpRequestInterceptor()));
        return restTemplate;
    }
}
