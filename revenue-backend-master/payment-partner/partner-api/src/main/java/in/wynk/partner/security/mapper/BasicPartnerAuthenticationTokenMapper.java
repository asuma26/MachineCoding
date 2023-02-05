package in.wynk.partner.security.mapper;

import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.mapper.AbstractPreAuthTokenMapper;
import in.wynk.partner.security.token.BasicPartnerAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class BasicPartnerAuthenticationTokenMapper extends AbstractPreAuthTokenMapper {

    public static final String AUTHENTICATION_SCHEME_BASIC = "Basic";

    @Override
    public Authentication parse(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        if (header == null) {
            return null;
        }

        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
            return null;
        }

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH007,
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH007, "Invalid basic authentication token");
        }
        return new BasicPartnerAuthenticationToken(token.substring(0, delim), token.substring(delim + 1));
    }

    @Override
    public void validate(Authentication authentication) throws WynkAuthenticationException {
        if (Objects.nonNull(authentication) && (!Objects.nonNull(authentication.getPrincipal()) || !Objects.nonNull(authentication.getPrincipal()))) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH009);
        }
    }

}
