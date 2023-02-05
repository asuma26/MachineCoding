package in.wynk.coupon.core.constant;

import in.wynk.exception.IWynkErrorType;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;

public enum CouponErrorType implements IWynkErrorType {

    CP001("Coupon Eligibility Failure", "Unable to evaluate coupon eligibility", HttpStatus.INTERNAL_SERVER_ERROR, CouponLoggingMarker.COUPON_ELIGIBILITY_ERROR),
    CP002("Invalid Coupon", "coupon is not found", HttpStatus.BAD_REQUEST, CouponLoggingMarker.APPLY_COUPON_ERROR),
    CP003("Invalid Coupon", "No Such Coupon is applied", HttpStatus.BAD_REQUEST, CouponLoggingMarker.REMOVE_COUPON_ERROR),
    CP004("Invalid Coupon", "Coupon is not found to exhaust", HttpStatus.BAD_REQUEST, CouponLoggingMarker.EXHAUST_COUPON_ERROR);

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
    CouponErrorType(String errorTitle, String errorMsg, String redirectUrlProp, HttpStatus httpResponseStatus, Marker marker) {
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
    CouponErrorType(String errorTitle, String errorMsg, HttpStatus httpResponseStatus, Marker marker) {
        this.errorTitle = errorTitle;
        this.errorMsg = errorMsg;
        this.httpResponseStatusCode = httpResponseStatus;
        this.marker = marker;
    }

    public static CouponErrorType getWynkErrorType(String name) {
        return CouponErrorType.valueOf(name);
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
