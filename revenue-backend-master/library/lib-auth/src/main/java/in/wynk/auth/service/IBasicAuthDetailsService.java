package in.wynk.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface IBasicAuthDetailsService {
    Authentication loadBasicAuthDetails(Object principal) throws AuthenticationException;
}
