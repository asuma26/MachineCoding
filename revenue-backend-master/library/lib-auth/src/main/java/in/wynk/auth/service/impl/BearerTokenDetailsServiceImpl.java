package in.wynk.auth.service.impl;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.dto.PreAuthUserDetailAuthenticationToken;
import in.wynk.auth.exception.WynkAuthErrorType;
import in.wynk.auth.exception.WynkAuthenticationException;
import in.wynk.auth.service.IJwtTokenService;
import in.wynk.auth.service.ITokenDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BearerTokenDetailsServiceImpl implements ITokenDetailsService {

    private final IJwtTokenService jwtTokenService;

    public BearerTokenDetailsServiceImpl(IJwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Authentication loadTokenDetails(String token) throws AuthenticationException {
        try {
            boolean isValid = jwtTokenService.validate(token);
            if (isValid) {
                Jws<Claims> jws = jwtTokenService.parse(token);
                List<GrantedAuthority> grantedAuthorities = null;
                if (jws.getBody().get(AuthConstant.GRANTED_AUTHORITIES) != null) {
                    grantedAuthorities = ((ArrayList<String>) jws.getBody().get(AuthConstant.GRANTED_AUTHORITIES))
                                                                           .stream()
                                                                           .map(SimpleGrantedAuthority::new)
                                                                           .collect(Collectors.toList());
                }
                Authentication authentication = new PreAuthUserDetailAuthenticationToken(
                        jws.getBody().getSubject(),
                        null,
                        jws.getHeader(),
                        jws.getBody(),
                        grantedAuthorities);
                authentication.setAuthenticated(true);
                return authentication;
            }
        } catch (ExpiredJwtException eje){
            Claims claimsJws = eje.getClaims();
            Authentication authentication = new PreAuthUserDetailAuthenticationToken(claimsJws.getSubject());
            authentication.setAuthenticated(false);
            return authentication;
        }
        catch (Exception ex) {
            throw new WynkAuthenticationException(WynkAuthErrorType.AUTH003, ex);
        }
        return null;
    }

}
