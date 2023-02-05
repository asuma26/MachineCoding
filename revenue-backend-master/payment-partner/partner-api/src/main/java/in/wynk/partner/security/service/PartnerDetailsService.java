package in.wynk.partner.security.service;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.service.IBasicAuthDetailsService;
import in.wynk.auth.service.IClientDetailsService;
import in.wynk.common.constant.BaseConstants;
import in.wynk.partner.security.token.BasicPartnerAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PartnerDetailsService implements IBasicAuthDetailsService {

    private final IClientDetailsService<Client> clientDetailsService;

    @Override
    public Authentication loadBasicAuthDetails(Object principal) throws AuthenticationException {
        Optional<Client> optional = clientDetailsService.getClientDetails((String) principal);
        if (optional.isPresent()) {
            Client client = optional.get();
            if (client.getMeta(AuthConstant.IS_BASIC_SUPPORTED).isPresent()) {
                boolean isBasicSupported = client.<Boolean>getMeta(AuthConstant.IS_BASIC_SUPPORTED).get();
                if (isBasicSupported) {
                    return new BasicPartnerAuthenticationToken(client.getClientId(), client.getClientSecret(), this.getAuthorities(client));
                } else {
                    return new BasicPartnerAuthenticationToken(principal, BaseConstants.ANONYMOUS);
                }
            }
        }
        return new BasicPartnerAuthenticationToken(principal, BaseConstants.ANONYMOUS);
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
