package in.wynk.auth.mapper;

import in.wynk.auth.exception.WynkAuthenticationException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractPreAuthTokenMapper {

    public Authentication map(HttpServletRequest request) {
        Authentication authentication = parse(request);
        validate(authentication);
        return  authentication;
    }

    public abstract Authentication parse(HttpServletRequest request);

    public abstract void validate(Authentication authentication) throws WynkAuthenticationException;

}
