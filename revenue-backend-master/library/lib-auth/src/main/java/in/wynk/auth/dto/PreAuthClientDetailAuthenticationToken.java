package in.wynk.auth.dto;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public class PreAuthClientDetailAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal; // used for client secret
    private final String credentials; // used for digest
    private final Map<String, Object> headers;
    private final Map<String, Object> body;

    public PreAuthClientDetailAuthenticationToken(String principal,
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
