package in.wynk.order.core.constant;

import in.wynk.exception.IWynkErrorType;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;

public enum OrderErrorType implements IWynkErrorType {

    ORD001("Order Placement Failure", "Unknown order type is supplied", HttpStatus.PRECONDITION_FAILED, OrderLoggingMarker.ORDER_PLACEMENT_ERROR),
    ORD004("Order Fulfilment Failure", "Unable to provision plan", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_FULFILMENT_ERROR),
    ORD002("Order Placement Failure", "An error occurred while processing order", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_PLACEMENT_ERROR),
    ORD003("Order Placement Failure", "partner order is not found", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_PLACEMENT_ERROR),
    ORD005("Order Deferred Failure", "Unable to defer order", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_DEFERRED_ERROR),
    ORD006("Order Status Failure", "Unable to fetch wynk order", HttpStatus.NOT_FOUND, OrderLoggingMarker.ORDER_STATUS_ERROR),
    ORD007("Order Notification failure", "Unable to communicate payment partner", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_NOTIFICATION_ERROR),
    ORD008("Order Notification failure", "Partner details are not found", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_NOTIFICATION_ERROR),
    ORD009("Order Notification failure", "Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_NOTIFICATION_ERROR),
    ORD010("Order Placement Failure", "failure to fetch existing order via partner order id or wynk order id", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_PLACEMENT_ERROR),
    ORD011("Order Processing Failure", "Unable to upsert order", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_PLACEMENT_ERROR),
    ORD012("Order Fulfilment Failure", "Unable to unProvision plan", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_FULFILMENT_ERROR),
    ORD013("Order Status Failure", "Unable to fetch deferred order details", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_STATUS_ERROR),
    ORD014("Subscription Client Failure", "Unable to fetch active plan", HttpStatus.INTERNAL_SERVER_ERROR, OrderLoggingMarker.ORDER_STATUS_ERROR);
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
    private String redirectUrlProp;

    /**
     * The http response status.
     */
    private final HttpStatus httpResponseStatusCode;

    private final Marker marker;

    /**
     * Instantiates a new wynk error type.
     *
     * @param errorTitle         the error title
     * @param errorMsg           the error msg
     * @param httpResponseStatus the http response status
     */
    OrderErrorType(String errorTitle, String errorMsg, String redirectUrlProp, HttpStatus httpResponseStatus, Marker marker) {
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
    OrderErrorType(String errorTitle, String errorMsg, HttpStatus httpResponseStatus, Marker marker) {
        this.errorTitle = errorTitle;
        this.errorMsg = errorMsg;
        this.httpResponseStatusCode = httpResponseStatus;
        this.marker = marker;
    }

    public static OrderErrorType getWynkErrorType(String name) {
        return OrderErrorType.valueOf(name);
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
