package in.wynk.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface IUserDetailsService {

    Authentication loadUserDetails(Object principal) throws AuthenticationException;

}
