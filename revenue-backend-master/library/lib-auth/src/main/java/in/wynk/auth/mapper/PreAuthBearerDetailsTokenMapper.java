package in.wynk.auth.mapper;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

public class PreAuthBearerDetailsTokenMapper extends AbstractPreAuthTokenMapper {

    @Override
    public Authentication parse(HttpServletRequest request) {
        String bearer = request.getHeader(AuthConstant.JWT_TOKEN_HEADER);
        String preAuthToken = bearer.replace(AuthConstant.AUTH_TOKEN_PREFIX, "");
        PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(preAuthToken, null);
        validate(preAuthenticatedAuthenticationToken);
        return preAuthenticatedAuthenticationToken;
    }

    @Override
    public void validate(Authentication authentication) throws WynkAuthenticationException {
        String token = (String) authentication.getPrincipal();
        if (StringUtils.isBlank(token)) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH002);
        }
    }
}
