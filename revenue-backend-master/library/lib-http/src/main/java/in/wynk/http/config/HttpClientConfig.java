package in.wynk.http.config;

import in.wynk.http.config.properties.HttpProperties;
import in.wynk.http.config.properties.HttpProperties.HttpTemplateProperties;
import in.wynk.http.constant.HttpMarker;
import in.wynk.http.filter.RequestResponseLoggingFilter;
import in.wynk.http.interceptor.CommonHttpRequestInterceptor;
import in.wynk.http.template.WynkRestTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({HttpProperties.class})
public class HttpClientConfig implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientConfig.class);

    private final HttpProperties httpProperties;
    private final ConfigurableListableBeanFactory beanFactory;

    public HttpClientConfig(HttpProperties httpProperties, ApplicationContext context) {
        this.beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
        this.httpProperties = httpProperties;
    }

    @Override
    public void afterPropertiesSet() {
        if (httpProperties.isEnableTemplateRegistration()) {
            logger.info(HttpMarker.REST_TEMPLATE_REGISTRATION, "registering rest templates ....");
            if (httpProperties.getTemplates().size() > 0) {
                for (Map.Entry<String, HttpTemplateProperties> templateEntry : httpProperties.getTemplates().entrySet()) {
                    RestTemplate restTemplate = WynkRestTemplateBuilder.builder()
                            .withHttpTemplateProperties(templateEntry.getValue())
                            .withInterceptor(Collections.singletonList(new CommonHttpRequestInterceptor()))
                            .build();
                    beanFactory.registerSingleton(templateEntry.getKey(), restTemplate);
                    logger.info(HttpMarker.REST_TEMPLATE_REGISTRATION, "http template bean {} is registered.", templateEntry.getKey());
                }
            } else {
                logger.info(HttpMarker.REST_TEMPLATE_REGISTRATION, "0 http template registered");
            }
        }
    }

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilter() {
        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        RequestResponseLoggingFilter userFilter = new RequestResponseLoggingFilter();
        registrationBean.setFilter(userFilter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}
