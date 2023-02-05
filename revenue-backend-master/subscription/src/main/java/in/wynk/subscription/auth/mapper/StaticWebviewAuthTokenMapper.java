package in.wynk.subscription.auth.mapper;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.dto.PreAuthClientDetailAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public class StaticWebviewAuthTokenMapper extends AbstractPreAuthTokenMapper {

    @Override
    public Authentication parse(HttpServletRequest request) {
        PreAuthClientDetailAuthenticationToken authentication;
        String bsyDigestHeader = request.getHeader(AuthConstant.X_BSY_MG);
        authentication = new PreAuthClientDetailAuthenticationToken(null, bsyDigestHeader, null, null, null);
        validate(authentication);
        return authentication;
    }

    @Override
    public void validate(Authentication authentication) throws WynkAuthenticationException {
        PreAuthClientDetailAuthenticationToken token = (PreAuthClientDetailAuthenticationToken) authentication;
        if (StringUtils.isNotBlank(token.getCredentials())) {
            return;
        }
        throw new WynkAuthenticationException(WynkAuthErrorType.AUTH009);
    }

}
