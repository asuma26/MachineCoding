package in.wynk.utils.constant;

import in.wynk.exception.IWynkErrorType;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;

public enum WcfUtilsErrorType implements IWynkErrorType {

    WCF001("Invalid Id", "Can not find adClient with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.AD_CLIENTS_ERROR),
    WCF002("Invalid Id", "Can not find adProperties with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.AD_PROPERTIES_ERROR),
    WCF003("Invalid Id", "Can not find adRecommendations with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.AD_RECOMMENDATIONS_ERROR),
    WCF004("Invalid Id", "Can not find adsConfig with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.ADS_CONFIG_ERROR),
    WCF005("Invalid Id", "Can not find adConfigTestUser with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.AD_CONFIG_TEST_USER),
    WCF006("Invalid Id", "Can not find adMsisdnOperatorDetails with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.MSISDN_OPERATOR_DETAILS),
    WCF007("Invalid Id", "Can not find adTestingByPassNumber with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.TESTING_BY_PASS_NUMBERS),
    WCF008("Invalid Id", "Can not find Coupon Code Link with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.COUPON_CODE_LINK_ERROR),
    WCF009("Invalid Id", "Can not find Coupons with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.COUPONS_ERROR),
    WCF010("Invalid Id", "Can not find User Coupons Availed Records with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.USER_COUPON_AVAILED_RECORDS_ERROR),
    WCF011("Invalid Id", "Can not find User Coupons Records with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.USER_COUPON_RECORDS_ERROR),
    WCF012("Invalid Id", "Can not find Itunes Id Uid Mapping with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.ITUNES_ID_UID_MAPPING_ERROR),
    WCF013("Invalid Id", "Can not find Payments Method with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.PAYMENTS_METHOD_ERROR),
    WCF014("Invalid Id", "Can not find User Preferred Payment with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.USER_PREFERRED_PAYMENT_ERROR),
    WCF015("Invalid Id", "Can not find Offers with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.OFFERS_ERROR),
    WCF016("Invalid Id", "Can not find Partners with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.PARTNERS_ERROR),
    WCF017("Invalid Id", "Can not find Plans with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.PLANS_ERROR),
    WCF018("Invalid Id", "Can not find Products with given invalid id", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.PRODUCTS_ERROR),
    WCF019("Invalid Request", "Empty request fields received", HttpStatus.BAD_REQUEST, WcfUtilsLoggingMarkers.BRAND_CHANNEL_EMPTY_REQUEST),
    WCF020("Amazon Service Error", "Can not use amazon service", HttpStatus.INTERNAL_SERVER_ERROR, WcfUtilsLoggingMarkers.AMAZON_SERVICE_ERROR),
    WCF021("Client Exception Error", "Client Exception Occurred", HttpStatus.INTERNAL_SERVER_ERROR, WcfUtilsLoggingMarkers.SDK_CLIENT_ERROR);


    private final String errorTitle;
    private final String errorMsg;
    private final HttpStatus httpResponseStatusCode;
    private final Marker marker;

    WcfUtilsErrorType(String errorTitle, String errorMsg, HttpStatus httpResponseStatusCode, Marker marker) {
        this.errorTitle = errorTitle;
        this.errorMsg = errorMsg;
        this.httpResponseStatusCode = httpResponseStatusCode;
        this.marker = marker;
    }

    private static WcfUtilsErrorType getWynkErrorType(String name) {
        return WcfUtilsErrorType.valueOf(name);
    }

    @Override
    public String getErrorCode() {
        return this.name();
    }

    @Override
    public String getErrorTitle() {
        return errorTitle;
    }

    @Override
    public String getErrorMessage() {
        return errorMsg;
    }

    @Override
    public HttpStatus getHttpResponseStatusCode() {
        return httpResponseStatusCode;
    }

    @Override
    public Marker getMarker() {
        return marker;
    }
}
