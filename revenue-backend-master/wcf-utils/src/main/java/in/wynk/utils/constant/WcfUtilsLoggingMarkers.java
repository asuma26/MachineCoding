package in.wynk.utils.constant;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface WcfUtilsLoggingMarkers extends BaseLoggingMarkers {

    Marker AD_CLIENTS_ERROR = MarkerFactory.getMarker("AD_CLIENTS_ERROR");
    Marker AD_PROPERTIES_ERROR = MarkerFactory.getMarker("AD_PROPERTIES_ERROR");
    Marker AD_RECOMMENDATIONS_ERROR = MarkerFactory.getMarker("AD_RECOMMENDATIONS_ERROR");
    Marker ADS_CONFIG_ERROR = MarkerFactory.getMarker("ADS_CONFIG_ERROR");
    Marker AD_CONFIG_TEST_USER = MarkerFactory.getMarker("AD_CONFIG_TEST_USER");
    Marker MSISDN_OPERATOR_DETAILS = MarkerFactory.getMarker("MSISDN_OPERATOR_DETAILS");
    Marker TESTING_BY_PASS_NUMBERS = MarkerFactory.getMarker("TESTING_BY_PASS_NUMBERS");
    Marker COUPON_CODE_LINK_ERROR = MarkerFactory.getMarker("COUPON_CODE_LINK_ERROR");
    Marker COUPONS_ERROR = MarkerFactory.getMarker("COUPONS_ERROR");
    Marker USER_COUPON_AVAILED_RECORDS_ERROR = MarkerFactory.getMarker("USER_COUPON_AVAILED_RECORDS_ERROR");
    Marker USER_COUPON_RECORDS_ERROR = MarkerFactory.getMarker("USER_COUPON_RECORDS_ERROR");
    Marker ITUNES_ID_UID_MAPPING_ERROR = MarkerFactory.getMarker("ITUNES_ID_UID_MAPPING_ERROR");
    Marker PAYMENTS_METHOD_ERROR = MarkerFactory.getMarker("PAYMENTS_METHOD_ERROR");
    Marker USER_PREFERRED_PAYMENT_ERROR = MarkerFactory.getMarker("USER_PREFERRED_PAYMENT_ERROR");
    Marker OFFERS_ERROR = MarkerFactory.getMarker("OFFERS_ERROR");
    Marker PARTNERS_ERROR = MarkerFactory.getMarker("PARTNERS_ERROR");
    Marker PLANS_ERROR = MarkerFactory.getMarker("PLANS_ERROR");
    Marker PRODUCTS_ERROR = MarkerFactory.getMarker("PRODUCTS_ERROR");
    Marker AMAZON_SERVICE_ERROR = MarkerFactory.getMarker("AMAZON_SERVICE_ERROR");
    Marker SDK_CLIENT_ERROR = MarkerFactory.getMarker("SDK_CLIENT_ERROR");
    Marker BRAND_CHANNEL_EMPTY_REQUEST = MarkerFactory.getMarker("BRAND_CHANNEL_EMPTY_REQUEST");
}
