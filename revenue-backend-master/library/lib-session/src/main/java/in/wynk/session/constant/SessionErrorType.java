package in.wynk.session.constant;

import in.wynk.exception.IWynkErrorType;
import in.wynk.exception.WynkErrorType;
import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;

public enum SessionErrorType implements IWynkErrorType {

    /** Error type for Invalid value */
    SESSION001("Session Not Found", "Unable to locate session in redis", HttpStatus.NOT_FOUND, BaseLoggingMarkers.APPLICATION_ERROR),
    SESSION002("Invalid Session Id", "Unable to parse session id", HttpStatus.BAD_REQUEST, BaseLoggingMarkers.APPLICATION_ERROR),
    SESSION003("Empty Session Id", "Session id is not provided", HttpStatus.PRECONDITION_FAILED, BaseLoggingMarkers.APPLICATION_ERROR),
    SESSION004("Persist Session Failed", "Unable to persist session in redis", HttpStatus.INTERNAL_SERVER_ERROR, BaseLoggingMarkers.APPLICATION_ERROR);
    /** The error title. */
    private final String errorTitle;

    /** The error msg. */
    private final String errorMsg;

    /** The http response status. */
    private final HttpStatus  httpResponseStatusCode;

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
    SessionErrorType(String errorTitle, String errorMsg, HttpStatus httpResponseStatus, Marker marker) {
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
