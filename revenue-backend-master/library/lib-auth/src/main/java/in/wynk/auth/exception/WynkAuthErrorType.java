package in.wynk.auth.exception;

import in.wynk.auth.constant.AuthLoggingMarker;
import in.wynk.exception.IWynkErrorType;
import in.wynk.exception.WynkErrorType;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;

public enum WynkAuthErrorType implements IWynkErrorType {

    /**
     * Error type for Invalid value
     */
    AUTH001("Key Store Failure", "Unable to load keys from key store", HttpStatus.INTERNAL_SERVER_ERROR, AuthLoggingMarker.BEAN_INITIALIZATION_ERROR),
    AUTH002("Jwt Token Undefined", "Unable to authorize jwt token, Token is not supplied", HttpStatus.PRECONDITION_FAILED, AuthLoggingMarker.APPLICATION_INVALID_USECASE),
    AUTH003("Invalid Jwt Token", "Jwt token authorization failure, Supplied jwt token is invalid or expired", HttpStatus.BAD_REQUEST, AuthLoggingMarker.APPLICATION_INVALID_USECASE),
    AUTH004("User Details Undefined", "Unable to authenticate, Pre auth user details is not found in the request", HttpStatus.PRECONDITION_REQUIRED, AuthLoggingMarker.APPLICATION_INVALID_USECASE),
    AUTH005("Signature Generation Failure", "Unable to authenticate, Failed to generate signature from pre auth user details", HttpStatus.INTERNAL_SERVER_ERROR, AuthLoggingMarker.APPLICATION_INVALID_USECASE),
    AUTH006("Invalid Signature", "Unable to authenticate, Supplied signature doesn't match", HttpStatus.BAD_REQUEST, AuthLoggingMarker.APPLICATION_INVALID_USECASE),
    AUTH007("Invalid Request", "Invalid request Parameter", HttpStatus.PRECONDITION_FAILED, AuthLoggingMarker.PRE_CONDITION_FAILURE),
    AUTH008("Invalid partner id", "Unable to determine secret corresponding to given partner id", HttpStatus.BAD_REQUEST, AuthLoggingMarker.APPLICATION_INVALID_USECASE),
    AUTH009("Client Details Undefined", "Unable to authenticate, Pre auth client details is not found in the request", HttpStatus.PRECONDITION_REQUIRED, AuthLoggingMarker.APPLICATION_INVALID_USECASE),
    ;
    /**
     * The error title.
     */
    private final String errorTitle;

    /**
     * The error msg.
     */
    private final String errorMsg;

    /**
     * The http response status.
     */
    private final HttpStatus httpResponseStatusCode;

    private final Marker marker;

    /**
     * Instantiates a new wynk error type.
     *
     * @param errorTitle
     *            the error title
     * @param errorMsg
     *            the error msg
     * @param httpResponseStatus
     *            the http response status
     */
    WynkAuthErrorType(String errorTitle, String errorMsg, HttpStatus httpResponseStatus, Marker marker) {
        this.errorTitle = errorTitle;
        this.errorMsg = errorMsg;
        this.httpResponseStatusCode = httpResponseStatus;
        this.marker = marker;
    }

    public static WynkErrorType getWynkErrorType(String name) {
        return WynkErrorType.valueOf(name);
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    @Override
    public String getErrorCode() {
        return this.name();
    }

    /**
     * Gets the error title.
     *
     * @return the error title
     */
    @Override
    public String getErrorTitle() {
        return errorTitle;
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    @Override
    public String getErrorMessage() {
        return errorMsg;
    }

    /**
     * Gets the http response status.
     *
     * @return the http response status
     */
    @Override
    public HttpStatus getHttpResponseStatusCode() {
        return httpResponseStatusCode;
    }

    @Override
    public Marker getMarker() {
        return marker;
    }

    @Override
    public String toString() {
        return "{" + "errorTitle:'" + errorTitle + '\'' + ", errorMsg:'" + errorMsg + '\'' + ", httpResponseStatusCode" + httpResponseStatusCode + '}';
    }

}
