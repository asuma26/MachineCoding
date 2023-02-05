package in.wynk.auth.service.impl;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.constant.AuthLoggingMarker;
import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.dto.S2SAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.service.IClientDetailsService;
import in.wynk.auth.service.IS2SDetailsService;
import in.wynk.auth.utils.EncryptUtils;
import in.wynk.http.request.WynkRequestContext;
import in.wynk.logging.BaseLoggingMarkers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static in.wynk.auth.exception.WynkAuthErrorType.AUTH008;


/**
 * @author Abhishek
 * @created 28/08/20
 */

@Slf4j
public class S2SClientDetailsService<T extends Client> implements IS2SDetailsService {

    private static final long REQUEST_GRACE_PERIOD = 20 * 1000;

    private final IClientDetailsService<T> clientDetailsService;

    public S2SClientDetailsService(IClientDetailsService<T> clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    public Authentication loadS2SDetails(Object principal) throws AuthenticationException {
        HttpServletRequest request = WynkRequestContext.getRequest();
        String authToken;
        long requestTimestamp;
        if (StringUtils.isNotBlank(request.getHeader(AuthConstant.X_WYNK_ATKN))) {
            authToken = request.getHeader(AuthConstant.X_WYNK_ATKN);
            requestTimestamp = Long.parseLong(request.getHeader(AuthConstant.X_WYNK_DATE));
        } else {
            authToken = request.getHeader(AuthConstant.X_BSY_ATKN);
            requestTimestamp = NumberUtils.toLong(request.getHeader(AuthConstant.X_BSY_DATE));
        }
        String partnerId = authToken.split(AuthConstant.DELIMITER)[0];
        Client client = getClientDetails(partnerId);
        if (System.currentTimeMillis() > requestTimestamp + REQUEST_GRACE_PERIOD) {
            log.error(AuthLoggingMarker.AUTHENTICATION_FAILURE, "request is exceed threshold {}", (System.currentTimeMillis() - (requestTimestamp + REQUEST_GRACE_PERIOD)));
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH007, "Request Timestamp exceeded allowed grace");
        }
        try {
            StringBuilder data = new StringBuilder(request.getMethod()).append(request.getRequestURI());
            if (StringUtils.isNotBlank(request.getQueryString())) {
                data.append("?").append(request.getQueryString());
            }
            data.append(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())))
                    .append(requestTimestamp);
            String credentials = EncryptUtils.calculateRFC2104HMAC(data.toString(), client.getClientSecret());
            return new S2SAuthenticationToken(client.getClientId(), credentials, null, null, getAuthorities(client));
        } catch (Exception ex) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH005, ex);
        }
    }

    private Client getClientDetails(String partnerId) {
        Optional<T> clientDetails = clientDetailsService.getClientDetails(partnerId);
        if (clientDetails.isPresent()) {
            return clientDetails.get();
        } else {
            throw new WynkAuthenticationException(AUTH008);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Client client) {
        List<GrantedAuthority> grantedAuthorities = null;
        if (client != null) {
            List<String> authorities = client.getAuthorities();
            if (!CollectionUtils.isEmpty(authorities)) {
                grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            }
        }
        return grantedAuthorities;
    }
}
