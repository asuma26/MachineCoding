package in.wynk.targeting.security.service;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.dto.PreAuthUserDetailAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.service.IUserDetailsService;
import in.wynk.auth.utils.EncryptUtils;
import in.wynk.http.request.WynkRequestContext;
import in.wynk.targeting.core.constant.AppConstant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class UserDetailsServiceImpl implements IUserDetailsService {

    @Override
    public Authentication loadUserDetails(Object principal) throws AuthenticationException {
        HttpServletRequest request = WynkRequestContext.getRequest();
        try {
            String deviceId = request.getHeader(AppConstant.AUTH_DID_HEADER);
            String[] tokens = deviceId.split(AuthConstant.PIPE_DELIMITER);
            String data = request.getMethod() +
                    request.getRequestURI() +
                    request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            String credentials = EncryptUtils.calculateRFC2104HMAC(data, tokens[0]);
            return new PreAuthUserDetailAuthenticationToken((String) principal, credentials, null, null, getAuthorities());
        } catch (Exception ex) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH005, ex);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(AuthConstant.AUTH_READ_ACCESS_ROLE);
        return Collections.singletonList(grantedAuthority);
    }

}
