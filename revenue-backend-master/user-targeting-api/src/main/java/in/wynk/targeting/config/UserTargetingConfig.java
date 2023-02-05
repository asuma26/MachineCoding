package in.wynk.targeting.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.wynk.auth.config.properties.SecurityProperties;
import in.wynk.auth.constant.BeanConstant;
import in.wynk.auth.factory.KeyPairFactory;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import in.wynk.auth.mapper.PreAuthBearerDetailsTokenMapper;
import in.wynk.auth.provider.BearerTokenAuthenticationProvider;
import in.wynk.auth.provider.UserDetailsAuthenticationProvider;
import in.wynk.auth.service.IJwtTokenService;
import in.wynk.auth.service.ITokenDetailsService;
import in.wynk.auth.service.IUserDetailsService;
import in.wynk.auth.service.impl.BearerTokenDetailsServiceImpl;
import in.wynk.auth.service.impl.JwtTokenServiceImpl;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.client.service.ClientDetailsCachingService;
import in.wynk.common.context.WynkApplicationContext;
import in.wynk.data.config.WynkCassandraBuilder;
import in.wynk.data.config.WynkMongoDbFactoryBuilder;
import in.wynk.data.config.properties.CassandraProperties;
import in.wynk.data.config.properties.MongoProperties;
import in.wynk.targeting.security.mapper.PreAuthUserDetailsTokenMapper;
import in.wynk.targeting.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.context.request.RequestContextListener;

import java.security.KeyPair;

@Configuration
@EnableScheduling
@EnableMongoRepositories(basePackages = "in.wynk.targeting.core.dao.repository.mongo", mongoTemplateRef = "adsMongoTemplate")
@EnableCassandraRepositories(basePackages = "in.wynk.targeting.core.dao.repository.cassandra", cassandraTemplateRef = "adTechCassandraOperations")
public class UserTargetingConfig {

    @Value("${spring.application.name}")
    private String applicationAlias;

    @Bean
    public WynkApplicationContext myApplicationContext(ClientDetailsCachingService cachingService) {
        ClientDetails client = (ClientDetails) cachingService.getClientByAlias(applicationAlias);
        return WynkApplicationContext.builder()
                .meta(client.getMeta())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .build();
    }

    @Bean
    public IUserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean(BeanConstant.PRE_AUTH_USER_DETAILS_TOKEN_MAPPER)
    public AbstractPreAuthTokenMapper preAuthTokenMapper() {
        return new PreAuthUserDetailsTokenMapper();
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean(name = {"adsMongoDbFactory", "mongoDbFactory"})
    @Primary
    public MongoDbFactory adsMongoDbFactory(MongoProperties mongoProperties) {
        return WynkMongoDbFactoryBuilder.buildMongoDbFactory(mongoProperties, "ut");
    }

    @Bean(name = {"adsMongoTemplate", "mongoTemplate"})
    public MongoTemplate adsMongoTemplate(MongoDbFactory adsMongoDbFactory) {
        return new MongoTemplate(adsMongoDbFactory);
    }

    @Bean(name = "testConfigMongoDbFactory")
    public MongoDbFactory testConfigMongoDbFactory(MongoProperties mongoProperties) {
        return WynkMongoDbFactoryBuilder.buildMongoDbFactory(mongoProperties, "testConfig");
    }

    @Bean(name = "testConfigMongoTemplate")
    public MongoTemplate testConfigMongoTemplate(@Qualifier("testConfigMongoDbFactory") MongoDbFactory testConfigMongoDbFactory) {
        return new MongoTemplate(testConfigMongoDbFactory);
    }

    @Bean
    public CassandraOperations adTechCassandraOperations(CassandraProperties cassandraProperties){
        return WynkCassandraBuilder.cassandraOperations(cassandraProperties.get("adtech"));
    }

    @Bean
    public Gson gson(){
        return new GsonBuilder().disableHtmlEscaping().create();
    }

    @Profile("!local")
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor("ut-event"));
        return eventMulticaster;
    }


    @Bean(BeanConstant.PRE_AUTH_JWT_DETAILS_TOKEN_MAPPER)
    public AbstractPreAuthTokenMapper preAuthBearerTokenMapper() {
        return new PreAuthBearerDetailsTokenMapper();
    }

    @Bean
    public AuthenticationProvider userAuthenticationProvider(IUserDetailsService userDetailsService) {
        return new UserDetailsAuthenticationProvider(userDetailsService);
    }

    @Bean
    public AuthenticationProvider tokenAuthenticationProvider(ITokenDetailsService tokenDetailsService) {
        return new BearerTokenAuthenticationProvider(tokenDetailsService);
    }

    @Bean
    public ITokenDetailsService tokenDetailsService(IJwtTokenService jwtTokenService) {
        return new BearerTokenDetailsServiceImpl(jwtTokenService);
    }

    @Bean
    public IJwtTokenService jwtTokenService(SecurityProperties securityProperties) {
        KeyPair keyPair = keyPair(securityProperties, keyStoreKeyFactory(securityProperties));
        return new JwtTokenServiceImpl(keyPair, securityProperties);
    }

    private KeyPair keyPair(SecurityProperties securityProperties, KeyPairFactory keyPairFactory) {
        return keyPairFactory.getKeyPair(securityProperties.getOauth().getSecrets().getKeyPairAlias(), securityProperties.getOauth().getSecrets().getKeyPairPassword().toCharArray());
    }

    public KeyPairFactory keyStoreKeyFactory(SecurityProperties securityProperties) {
        return new KeyPairFactory(securityProperties.getOauth().getSecrets().getKeyStore(),
                securityProperties.getOauth().getSecrets().getKeyStorePassword().toCharArray());
    }


}
