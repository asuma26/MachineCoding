package in.wynk.partner.security.filter;

import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import in.wynk.partner.security.mapper.BasicPartnerAuthenticationTokenMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BasicPartnerAuthenticationFilter extends BasicAuthenticationFilter {

    private final AbstractPreAuthTokenMapper authenticationConverter = new BasicPartnerAuthenticationTokenMapper();

    public BasicPartnerAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authRequest = authenticationConverter.map(request);
        if (authRequest == null) {
            chain.doFilter(request, response);
            return;
        }
        Authentication authResult = super.getAuthenticationManager().authenticate(authRequest);
        if (authResult.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authResult);
            this.onSuccessfulAuthentication(request, response, authResult);
        }
        chain.doFilter(request, response);
    }
}
