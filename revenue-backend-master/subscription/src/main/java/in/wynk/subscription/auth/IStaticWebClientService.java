package in.wynk.subscription.auth;

import in.wynk.auth.dao.entity.Client;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

public interface IStaticWebClientService {

    Authentication loadClientSignature() throws AuthenticationException;
    Optional<Client> getClient();
}

