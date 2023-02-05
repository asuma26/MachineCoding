package in.wynk.auth.constant;

public interface AuthConstant {

    String KEY_TYPE = "jks";
    String DELIMITER = ":";
    String PIPE_DELIMITER = "\\|";
    String AUTH_HTTP_POST = "POST";
    String ENCRYPTION_ALGORITHM_TYPE = "RSA";
    String AUTH_ENDPOINT = "/api/v1/authenticate";
    String AUTH_TOKEN_PREFIX = "Bearer ";
    String GRANTED_AUTHORITIES = "ga";
    String JWT_TOKEN_HEADER = "x-auth-token";
    String SECURITY_PROPS_PREFIX = "security";
    String UNAUTHORIZED_ACCESS = "Unauthorized";
    String X_BSY_DATE = "x-bsy-date";
    String X_WYNK_DATE = "x-wynk-timestamp";
    String X_BSY_ATKN = "x-bsy-atkn";
    String X_WYNK_ATKN = "x-wynk-atkn";
    String AUTH_READ_ACCESS_ROLE = "READ_ACCESS";
    String S2S_READ_ACCESS_ROLE = "S2S_READ_ACCESS";
    String S2S_WRITE_ACCESS_ROLE = "S2S_WRITE_ACCESS";
    String X_BSY_MG="x-bsy-mg";
    String X_WYNK_UID="x-wynk-uid";
    String X_BSY_PATH = "x-bsy-path";
    String IS_BASIC_SUPPORTED = "isBasicAuthSupported";
}
