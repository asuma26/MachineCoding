package in.wynk.auth.entrypoint;

import in.wynk.auth.constant.AuthConstant;
import in.wynk.auth.constant.AuthLoggingMarker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthenticationFailureEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error(AuthLoggingMarker.AUTHENTICATION_FAILURE, authException.getMessage() + '-' + request.getRequestURI() , authException);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, AuthConstant.UNAUTHORIZED_ACCESS);
    }
}
