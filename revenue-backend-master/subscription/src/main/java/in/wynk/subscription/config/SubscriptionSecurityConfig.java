package in.wynk.subscription.config;

import in.wynk.auth.config.properties.SecurityProperties;
import in.wynk.auth.constant.BeanConstant;
import in.wynk.auth.entrypoint.AuthenticationFailureEntryPoint;
import in.wynk.auth.filter.S2SDetailsAuthenticationFilter;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import in.wynk.subscription.auth.filter.StaticWebAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SubscriptionSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProperties properties;

    private final AuthenticationProvider s2sAuthenticationProvider;

    private final AbstractPreAuthTokenMapper preAuthS2SDetailsTokenMapper;

    private final AuthenticationFailureEntryPoint authenticationFailureEntryPoint;

    private final AuthenticationProvider clientToServerAuthenticationProvider;

    private final AbstractPreAuthTokenMapper preAuthClientToServerDetailsTokenMapper;

    public SubscriptionSecurityConfig(SecurityProperties properties,
                                      AuthenticationProvider s2sAuthenticationProvider,
                                      AuthenticationFailureEntryPoint authenticationFailureEntryPoint,
                                      @Qualifier(BeanConstant.PRE_AUTH_S2S_DETAILS_TOKEN_MAPPER) AbstractPreAuthTokenMapper preAuthS2SDetailsTokenMapper,
                                      AuthenticationProvider clientToServerAuthenticationProvider,
                                      @Qualifier(BeanConstant.PRE_AUTH_CLIENT_DETAILS_TOKEN_MAPPER) AbstractPreAuthTokenMapper preAuthClientToServerDetailsTokenMapper) {
        this.properties = properties;
        this.s2sAuthenticationProvider = s2sAuthenticationProvider;
        this.preAuthS2SDetailsTokenMapper = preAuthS2SDetailsTokenMapper;
        this.authenticationFailureEntryPoint = authenticationFailureEntryPoint;
        this.clientToServerAuthenticationProvider = clientToServerAuthenticationProvider;
        this.preAuthClientToServerDetailsTokenMapper = preAuthClientToServerDetailsTokenMapper;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (properties.getExempt() != null
                && properties.getExempt().getPaths() != null
                && properties.getExempt().getPaths().size() > 0) {
            http
                    .authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
                    .antMatchers(properties.getExempt().getPaths().toArray(new String[]{}))
                    .permitAll();
        }

        http
                .csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationFailureEntryPoint)
                .and()
                .addFilter(new S2SDetailsAuthenticationFilter(authenticationManagerBean(), preAuthS2SDetailsTokenMapper))
                .addFilter(new StaticWebAuthenticationFilter(authenticationManagerBean(), preAuthClientToServerDetailsTokenMapper));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(s2sAuthenticationProvider);
        auth.authenticationProvider(clientToServerAuthenticationProvider);
    }

}
