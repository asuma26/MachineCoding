package in.wynk.partner.security.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class BasicPartnerAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final Object credentials;

    public BasicPartnerAuthenticationToken(Object principal, Object credentials) {
        this(principal, credentials, null);
    }

    public BasicPartnerAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> grantedAuthorities) {
        super(grantedAuthorities);
        this.principal = principal;
        this.credentials = credentials;
    }

}
