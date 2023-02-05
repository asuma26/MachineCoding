package in.wynk.targeting.security.mapper;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.dto.PreAuthUserDetailAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import in.wynk.targeting.core.constant.AppConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public class PreAuthUserDetailsTokenMapper extends AbstractPreAuthTokenMapper {

    @Override
    public Authentication parse(HttpServletRequest request) {
        PreAuthUserDetailAuthenticationToken authentication;
        String credentials = request.getHeader(AppConstant.AUTH_UTKN_HEADER);
        String[] tokens = new String[2];
        if (credentials != null) {
            tokens = credentials.split(AuthConstant.DELIMITER);
        }
        authentication = new PreAuthUserDetailAuthenticationToken(tokens[0], tokens[1], null, null, null);
        validate(authentication);
        return authentication;
    }

    @Override
    public void validate(Authentication authentication) throws WynkAuthenticationException {
        PreAuthUserDetailAuthenticationToken token = (PreAuthUserDetailAuthenticationToken) authentication;
        if (!StringUtils.isBlank(token.getPrincipal()) && !StringUtils.isBlank(token.getCredentials())) {
            return;
        }
        throw new WynkAuthenticationException(WynkAuthErrorType.AUTH004);
    }

}
