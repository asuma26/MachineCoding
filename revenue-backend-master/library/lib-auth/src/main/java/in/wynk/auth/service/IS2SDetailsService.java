package in.wynk.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface IS2SDetailsService {

    Authentication loadS2SDetails(Object principal) throws AuthenticationException;

}

