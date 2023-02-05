package in.wynk.auth.dto;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public class S2SAuthenticationToken extends AbstractAuthenticationToken {

    //TODO: Create wynk principal with id and name
    private final String principal;
    private final String credentials;
    private final Map<String, Object> headers;
    private final Map<String, Object> body;

    public S2SAuthenticationToken(String principal,
                                  String credentials,
                                  Map<String, Object> headers,
                                  Map<String, Object> body,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.body = body;
        this.principal = principal;
        this.credentials = credentials;
        this.headers = headers;
    }

    @Override
    public String getCredentials() {
        return credentials;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Map<String, Object> getBody() {
        return body;
    }
}
