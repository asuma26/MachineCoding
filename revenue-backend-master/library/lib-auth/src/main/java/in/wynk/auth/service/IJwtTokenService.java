package in.wynk.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

import java.util.Map;

public interface IJwtTokenService {

    String create(String subject, Map<String, Object> headers, Map<String, Object> claims);

     boolean validate(String token) throws JwtException;

     Jws<Claims> parse(String token) throws JwtException;

}
