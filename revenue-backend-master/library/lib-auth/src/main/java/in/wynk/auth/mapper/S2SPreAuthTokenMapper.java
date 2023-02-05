package in.wynk.auth.mapper;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.dto.S2SAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public class S2SPreAuthTokenMapper extends AbstractPreAuthTokenMapper {

    @Override
    public Authentication parse(HttpServletRequest request) {
        String credentials = request.getHeader(AuthConstant.X_BSY_ATKN);
        if (!StringUtils.isNotEmpty(credentials)) {
            credentials = request.getHeader(AuthConstant.X_WYNK_ATKN);
        }
        String[] tokens = new String[2];
        if (credentials != null) {
            tokens = credentials.split(AuthConstant.DELIMITER);
        }
        S2SAuthenticationToken authentication = new S2SAuthenticationToken(tokens[0], tokens[1], null, null, null);
        return authentication;
    }

    @Override
    public void validate(Authentication authentication) throws WynkAuthenticationException {
        S2SAuthenticationToken token = (S2SAuthenticationToken) authentication;
        if (!StringUtils.isBlank(token.getPrincipal()) && !StringUtils.isBlank(token.getCredentials())) {
            return;
        }
        throw new WynkAuthenticationException(WynkAuthErrorType.AUTH004);
    }
}
