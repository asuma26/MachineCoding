package in.wynk.client.config;

import in.wynk.auth.constant.BeanConstant;
import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.mapper.S2SPreAuthTokenMapper;
import in.wynk.auth.provider.S2SAuthenticationProvider;
import in.wynk.auth.service.IClientDetailsService;
import in.wynk.auth.service.IS2SDetailsService;
import in.wynk.auth.service.impl.S2SClientDetailsService;
import in.wynk.client.service.ClientDetailsCachingService;
import in.wynk.client.service.ClientDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;

@Configuration
@EnableScheduling
public class ClientConfig {

    @Bean
    public IClientDetailsService<Client> clientDetailsService(ClientDetailsCachingService clientDetailsCachingService) {
        return new ClientDetailsService(clientDetailsCachingService);
    }

    @Bean
    public IS2SDetailsService s2sDetailsService(IClientDetailsService<Client> clientDetailsService) {
        return new S2SClientDetailsService<>(clientDetailsService);
    }

    @Bean(BeanConstant.PRE_AUTH_S2S_DETAILS_TOKEN_MAPPER)
    public S2SPreAuthTokenMapper preAuthS2STokenMapper() {
        return new S2SPreAuthTokenMapper();
    }

    @Bean
    public AuthenticationProvider s2sAuthenticationProvider(IS2SDetailsService s2sDetailsService) {
        return new S2SAuthenticationProvider(s2sDetailsService);
    }

}
