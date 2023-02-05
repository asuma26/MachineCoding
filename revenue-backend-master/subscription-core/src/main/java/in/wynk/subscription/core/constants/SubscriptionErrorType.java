package in.wynk.subscription.core.constants;

import in.wynk.exception.IWynkErrorType;
import in.wynk.exception.WynkErrorType;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;

public enum SubscriptionErrorType implements IWynkErrorType {

    /**
     * PAYU ERROR CODES
     **/
    SUB001("Subscription Provision Failure", "transaction status is not successful", HttpStatus.PAYMENT_REQUIRED, SubscriptionLoggingMarkers.MANUAL_SUBSCRIPTION_PROVISIONING_FAILURE),
    SUB002("Subscription Provision Failure", "transaction status api return non 200 response", HttpStatus.PAYMENT_REQUIRED, SubscriptionLoggingMarkers.MANUAL_SUBSCRIPTION_PROVISIONING_FAILURE),
    SUB003("Msisdn Decryption Failure", "given si can't be decrypted and normalized to form valid msisdn", HttpStatus.BAD_REQUEST, SubscriptionLoggingMarkers.INVALID_ENCRYPTED_MSISDN),
    SUB004("Subscription Add Msisdn Failure", "capi api return non 200 response", HttpStatus.BAD_REQUEST, SubscriptionLoggingMarkers.ADD_MSISDN_TO_COLLECTION_FAILURE),
    SUB005("Active Plan Details Failure", "no active subscription found for supplied plan id", HttpStatus.INTERNAL_SERVER_ERROR, SubscriptionLoggingMarkers.ACTIVE_SUBSCRIPTION_DETAILS),
    SUB997("Subscription Failure", "Unable to generate session, something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, SubscriptionLoggingMarkers.MANUAL_SUBSCRIPTION_SESSION_INIT_FAILURE);
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
     * @param errorTitle         the error title
     * @param errorMsg           the error msg
     * @param httpResponseStatus the http response status
     */
    SubscriptionErrorType(String errorTitle, String errorMsg, HttpStatus httpResponseStatus, Marker marker) {
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
