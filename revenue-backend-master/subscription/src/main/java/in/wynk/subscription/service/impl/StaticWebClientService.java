package in.wynk.subscription.service.impl;

import com.google.gson.Gson;
import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.dto.PreAuthClientDetailAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.service.IClientDetailsService;
import in.wynk.auth.utils.EncryptUtils;
import in.wynk.http.request.CachedBodyHttpServletRequest;
import in.wynk.http.request.WynkRequestContext;
import in.wynk.subscription.auth.IStaticWebClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

import static in.wynk.auth.constant.AuthConstant.X_WYNK_UID;


public class StaticWebClientService implements IStaticWebClientService {

    private static final long REQUEST_GRACE_PERIOD = 10 * 1000;

    @Autowired
    private IClientDetailsService<Client> clientDetailsService;

    @Autowired
    private Gson gson;

    @Override
    public Authentication loadClientSignature() throws AuthenticationException {
        CachedBodyHttpServletRequest request = WynkRequestContext.getRequest();
        long requestTimestamp = Long.parseLong(request.getHeader(AuthConstant.X_BSY_DATE));
        try {
            String uid=request.getHeader(X_WYNK_UID);
            StringBuilder data = new StringBuilder(request.getHeader(AuthConstant.X_BSY_PATH)).append(uid).append(requestTimestamp);
            Optional<Client> client = getClient();
            if(client.isPresent()){
                String resultSignature = EncryptUtils.calculateRFC2104HMAC(data.toString(), client.get().getClientSecret());
                String clientId = client.get().getClientId();
                return new PreAuthClientDetailAuthenticationToken(clientId, resultSignature, null, null, null);
            } else {
                throw new WynkAuthenticationException(WynkAuthErrorType.AUTH009);
            }
        } catch (Exception ex) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH005, ex);
        }
    }

    @Override
    public Optional<Client> getClient() {
        return clientDetailsService.getClientDetails("6ba1767f1b15f9748a914bcf765ba8b8");
    }

}
