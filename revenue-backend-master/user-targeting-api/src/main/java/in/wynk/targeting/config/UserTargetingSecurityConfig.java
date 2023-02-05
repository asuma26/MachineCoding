package in.wynk.targeting.config;

import in.wynk.auth.config.properties.SecurityProperties;
import in.wynk.auth.constant.BeanConstant;
import in.wynk.auth.entrypoint.AuthenticationFailureEntryPoint;
import in.wynk.auth.filter.BearerTokenAuthorizationFilter;
import in.wynk.auth.filter.S2SDetailsAuthenticationFilter;
import in.wynk.auth.filter.UserDetailAuthenticationFilter;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import in.wynk.auth.service.IJwtTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @author Abhishek
 * @created 16/09/20
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UserTargetingSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProperties properties;

    private final IJwtTokenService jwtTokenService;

    private final ApplicationEventPublisher eventPublisher;

    private final AuthenticationProvider userAuthenticationProvider;

    private final AuthenticationProvider tokenAuthenticationProvider;

    private final AuthenticationProvider s2sAuthenticationProvider;

    private final AbstractPreAuthTokenMapper preAuthJwtDetailsTokenMapper;

    private final AbstractPreAuthTokenMapper preAuthUserDetailsTokenMapper;

    private final AbstractPreAuthTokenMapper preAuthS2SDetailsTokenMapper;

    private final AuthenticationFailureEntryPoint authenticationFailureEntryPoint;

    public UserTargetingSecurityConfig(SecurityProperties properties,
                                       IJwtTokenService jwtTokenService,
                                       ApplicationEventPublisher eventPublisher,
                                       AuthenticationProvider userAuthenticationProvider,
                                       AuthenticationProvider tokenAuthenticationProvider,
                                       AuthenticationProvider s2sAuthenticationProvider,
                                       AuthenticationFailureEntryPoint authenticationFailureEntryPoint,
                                       @Qualifier(BeanConstant.PRE_AUTH_JWT_DETAILS_TOKEN_MAPPER) AbstractPreAuthTokenMapper preAuthJwtDetailsTokenMapper,
                                       @Qualifier(BeanConstant.PRE_AUTH_USER_DETAILS_TOKEN_MAPPER) AbstractPreAuthTokenMapper preAuthUserDetailsTokenMapper,
                                       @Qualifier(BeanConstant.PRE_AUTH_S2S_DETAILS_TOKEN_MAPPER) AbstractPreAuthTokenMapper preAuthS2SDetailsTokenMapper) {
        this.properties = properties;
        this.eventPublisher = eventPublisher;
        this.jwtTokenService = jwtTokenService;
        this.s2sAuthenticationProvider = s2sAuthenticationProvider;
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.tokenAuthenticationProvider = tokenAuthenticationProvider;
        this.preAuthJwtDetailsTokenMapper = preAuthJwtDetailsTokenMapper;
        this.preAuthUserDetailsTokenMapper = preAuthUserDetailsTokenMapper;
        this.preAuthS2SDetailsTokenMapper = preAuthS2SDetailsTokenMapper;
        this.authenticationFailureEntryPoint = authenticationFailureEntryPoint;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (properties.getExempt() != null
                && properties.getExempt().getPaths() != null
                && properties.getExempt().getPaths().size() > 0) {
            http
                    .authorizeRequests()
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
                .addFilter(new UserDetailAuthenticationFilter(jwtTokenService, authenticationManagerBean(), eventPublisher, preAuthUserDetailsTokenMapper))
                .addFilter(new BearerTokenAuthorizationFilter(authenticationManagerBean(),eventPublisher, preAuthJwtDetailsTokenMapper))
                .addFilter(new S2SDetailsAuthenticationFilter(authenticationManagerBean(), preAuthS2SDetailsTokenMapper));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(s2sAuthenticationProvider);
        auth.authenticationProvider(userAuthenticationProvider);
        auth.authenticationProvider(tokenAuthenticationProvider);
    }
}
