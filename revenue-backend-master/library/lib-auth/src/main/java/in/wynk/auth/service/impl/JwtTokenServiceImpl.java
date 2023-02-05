package in.wynk.auth.service.impl;

import in.wynk.auth.config.properties.SecurityProperties;
import in.wynk.auth.service.IJwtTokenService;
import io.jsonwebtoken.*;

import java.security.KeyPair;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class JwtTokenServiceImpl implements IJwtTokenService {

    private final KeyPair keyPair;
    private final SecurityProperties securityProperties;

    public JwtTokenServiceImpl(KeyPair keyPair, SecurityProperties securityProperties) {
        this.keyPair = keyPair;
        this.securityProperties = securityProperties;
    }

    @Override
    public String create(String subject, Map<String, Object> headers, Map<String, Object> claims) {
        return Jwts.builder()
                .setIssuer(securityProperties.getOauth().getJwt().getIssuer())
                .setSubject(subject)
                .addClaims(claims)
                .setHeader(headers)
                .setExpiration(new Date(System.currentTimeMillis() +
                        securityProperties.getOauth().getJwt().getExpiry()))
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate())
                .compact();
    }

    @Override
    public boolean validate(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .parseClaimsJws(token)
                .getBody()
                .getIssuer()
                .equals(securityProperties.getOauth().getJwt().getIssuer());

    }

    @Override
    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .parseClaimsJws(token);
    }
}
