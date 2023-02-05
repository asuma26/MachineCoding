package in.wynk.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface ITokenDetailsService {

    Authentication loadTokenDetails(String token) throws AuthenticationException;

}
