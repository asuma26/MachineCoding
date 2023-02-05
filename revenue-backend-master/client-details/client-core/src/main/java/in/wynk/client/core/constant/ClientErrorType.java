package in.wynk.client.core.constant;

import in.wynk.exception.IWynkErrorType;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;

public enum ClientErrorType implements IWynkErrorType {

    CLIENT001("Client Loading Failure", "Client is not found", HttpStatus.INTERNAL_SERVER_ERROR, ClientLoggingMarker.INVALID_CLIENT_DETAILS),
    CLIENT002("Client Loading Failure", "Unable to fetch client", HttpStatus.INTERNAL_SERVER_ERROR, ClientLoggingMarker.INVALID_CLIENT_DETAILS),
    CLIENT003("Client Loading Failure","Client is not found or Client does not have required secret",HttpStatus.INTERNAL_SERVER_ERROR,ClientLoggingMarker.INVALID_CLIENT_DETAILS);
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
     * The http response status.
     */
    private String redirectUrlProp;

    /**
     * Instantiates a new wynk error type.
     *
     * @param errorTitle         the error title
     * @param errorMsg           the error msg
     * @param httpResponseStatus the http response status
     */
    ClientErrorType(String errorTitle, String errorMsg, String redirectUrlProp, HttpStatus httpResponseStatus, Marker marker) {
        this.errorTitle = errorTitle;
        this.errorMsg = errorMsg;
        this.redirectUrlProp = redirectUrlProp;
        this.httpResponseStatusCode = httpResponseStatus;
        this.marker = marker;
    }


    /**
     * Instantiates a new wynk error type.
     *
     * @param errorTitle         the error title
     * @param errorMsg           the error msg
     * @param httpResponseStatus the http response status
     */
    ClientErrorType(String errorTitle, String errorMsg, HttpStatus httpResponseStatus, Marker marker) {
        this.errorTitle = errorTitle;
        this.errorMsg = errorMsg;
        this.httpResponseStatusCode = httpResponseStatus;
        this.marker = marker;
    }

    public static ClientErrorType getWynkErrorType(String name) {
        return ClientErrorType.valueOf(name);
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
     * Gets the redirect Url Prop.
     *
     * @return the redirect url prop
     */
    public String getRedirectUrlProp() {
        return redirectUrlProp;
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
