package in.wynk.exception;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;


/**
 * The Enum in.wynk.exception.WynkErrorType.
 */
public enum WynkErrorType implements IWynkErrorType {

    /**
     * Error type for Invalid value
     */
    UT001("Invalid value", "Unable to determine msisdn.", HttpStatus.BAD_REQUEST, BaseLoggingMarkers.INVALID_VALUE),

    /**
     * Error type for Authentication error
     */
    UT002("Authentication error", "Your PIN expired.", HttpStatus.BAD_GATEWAY, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /**
     * Error type for Empty request
     */
    UT003("Empty request", "Empty request", HttpStatus.NO_CONTENT, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /**
     * Error type for UID generation error
     */
    UT004("UID generation error", "Error in generating the uid", HttpStatus.INTERNAL_SERVER_ERROR, BaseLoggingMarkers.APPLICATION_ERROR),

    /**
     * Error type for Invalid value for productId
     */
    UT005("Invalid value for productId", "Invalid value for productId", HttpStatus.PRECONDITION_FAILED, BaseLoggingMarkers.INVALID_VALUE),


    /**
     * Error type for Failure in generating OTP
     */
    UT007("Failure in generating OTP", "Failure in generating OTP", HttpStatus.INTERNAL_SERVER_ERROR, BaseLoggingMarkers.APPLICATION_ERROR),

    /**
     * Error type for Error while making payment
     */
    UT008("Error while making payment", "Error while making payment", HttpStatus.INTERNAL_SERVER_ERROR, BaseLoggingMarkers.APPLICATION_ERROR),

    /**
     * Error type for Error while encoding the url
     */
    UT009("Error while encoding the url", "Error while encoding the url", HttpStatus.INTERNAL_SERVER_ERROR, BaseLoggingMarkers.APPLICATION_ERROR),

    /**
     * Error type for Error while extracting parameters
     */
    UT010("Error while extracting parameters", "Error while extracting parameters", HttpStatus.INTERNAL_SERVER_ERROR, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /**
     * Error type for Request mapping not found
     */
    UT011("Not found", "Request mapping not found", HttpStatus.NOT_FOUND, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /**
     * Error type for Service parameter missing
     */
    UT012("Service parameter missing", "Mandatory service parameter missing", HttpStatus.PRECONDITION_FAILED, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Error type for Unauthenticated request */
    UT013("Unauthenticated request", "Request not authenticated", HttpStatus.FORBIDDEN, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Error type for No such service exist for the specified uid */
    UT014("No such service exist", "No such service exist for the specified uid", HttpStatus.BAD_REQUEST, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Error type for Invalid Pin */
    UT015("Authentication error", "Invalid Pin", HttpStatus.BAD_REQUEST, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Error type for Not subscribed to the product */
    UT016("Not subscribed to the product", "Not subscribed to the product which you want to unsubscribe", HttpStatus.PRECONDITION_FAILED, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Error type for Not transaction present for orderid */
    UT017("No transaction present for orderid", "No transaction present for orderid", HttpStatus.PRECONDITION_FAILED, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Error type for Not Authorized to get the details of this transaction */
    UT018("Not Authorized", "Not Authorized to get the details of this transaction", HttpStatus.FORBIDDEN, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Error type for Unauthenticated request */
    UT019("Msisdn not registered", "Msisdn not registered", HttpStatus.FORBIDDEN, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Not a valid transaction id */
    UT020("Not a valid Transaction id", "Not a valid transaction id", HttpStatus.FORBIDDEN, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Illegal Argument */
    UT021("Illegal Argument", "Input is not valid", HttpStatus.PRECONDITION_FAILED, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    /** Error type for Unauthenticated request */
    UT022("User not registered", "User not registered", HttpStatus.FORBIDDEN, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    UT023("Subscription in progress", "Subscription in Progress", HttpStatus.FORBIDDEN, BaseLoggingMarkers.APPLICATION_INVALID_USECASE),

    UT024("Request Timeout error", "No Response from external service", HttpStatus.GATEWAY_TIMEOUT, BaseLoggingMarkers.SERVICE_PARTNER_ERROR),

    UT777("Unsuccessful response from internal API", "Internal API failure", HttpStatus.INTERNAL_SERVER_ERROR, BaseLoggingMarkers.APPLICATION_ERROR),

    UT888("Error in App Specific External API", "Error in App Specific External API", HttpStatus.INTERNAL_SERVER_ERROR, BaseLoggingMarkers.SERVICE_PARTNER_ERROR),

    /**
     * Error type for Internal Server Error
     */
    UT999("Oops! Something went wrong", "Oops! Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, BaseLoggingMarkers.APPLICATION_ERROR),

    RG777("Caching Failure", "Unable to cache plans", HttpStatus.BAD_REQUEST, BaseLoggingMarkers.APPLICATION_ERROR);

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
    WynkErrorType(String errorTitle, String errorMsg, HttpStatus httpResponseStatus, Marker marker) {
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
