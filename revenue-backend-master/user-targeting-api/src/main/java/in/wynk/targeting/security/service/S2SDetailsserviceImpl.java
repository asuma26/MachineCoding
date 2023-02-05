package in.wynk.targeting.security.service;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.dto.PreAuthUserDetailAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.service.IS2SDetailsService;
import in.wynk.auth.utils.EncryptUtils;
import in.wynk.http.request.WynkRequestContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Deprecated
public class S2SDetailsserviceImpl implements IS2SDetailsService {

    private static final Map<String, String> appIdMap;

    private static final long REQUEST_GRACE_PERIOD = 30 * 1000;

    static {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("543fbd6f96644406567079c00d8f33dc", "50de5a601c133a29c8db434fa9bf2db4");
        appIdMap = Collections.unmodifiableMap(tempMap);
    }

    @Override
    public Authentication loadS2SDetails(Object principal) throws AuthenticationException {
        HttpServletRequest request = WynkRequestContext.getRequest();
        long requestTimestamp = Long.parseLong(request.getHeader(AuthConstant.X_BSY_DATE));
        if (System.currentTimeMillis() > requestTimestamp + REQUEST_GRACE_PERIOD) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH007, "Request Timestamp exceeded allowed grace");
        }
        try {
            String appId = request.getHeader(AuthConstant.X_BSY_ATKN).split(AuthConstant.DELIMITER)[0];
            String secret = appIdMap.get(appId); // Fetch from map
            StringBuilder data = new StringBuilder(request.getMethod()).append(request.getRequestURI());
            if (StringUtils.isNotBlank(request.getQueryString())) {
                data.append("?").append(request.getQueryString());
            }
            data.append(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())))
                    .append(request.getHeader(AuthConstant.X_BSY_DATE));
            String credentials = EncryptUtils.calculateRFC2104HMAC(data.toString(), secret);
            return new PreAuthUserDetailAuthenticationToken((String) principal, credentials, null, null, getAuthorities());
        } catch (Exception ex) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH005, ex);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthConstant.S2S_READ_ACCESS_ROLE));
        authorities.add(new SimpleGrantedAuthority(AuthConstant.S2S_WRITE_ACCESS_ROLE));
        return authorities;
    }
}
