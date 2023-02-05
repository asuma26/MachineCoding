package in.wynk.subscription.core.constants;

import in.wynk.logging.BaseLoggingMarkers;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface SubscriptionLoggingMarkers extends BaseLoggingMarkers {

    Marker MSISDN_IDENTIFICATION_ERROR = MarkerFactory.getMarker("MSISDN_IDENTIFICATION_ERROR");
    Marker MANUAL_SUBSCRIPTION_PROVISIONING_FAILURE = MarkerFactory.getMarker("MANUAL_SUBSCRIPTION_PROVISIONING_FAILURE");
    Marker ADD_MSISDN_TO_COLLECTION_FAILURE = MarkerFactory.getMarker("ADD_MSISDN_TO_COLLECTION_FAILURE");
    Marker MANUAL_SUBSCRIPTION_SESSION_INIT_FAILURE = MarkerFactory.getMarker("MANUAL_SUBSCRIPTION_SESSION_INIT_FAILURE");
    Marker AIRTEL_TV_ERROR = MarkerFactory.getMarker("AIRTEL_TV_ERROR");
    Marker WYNK_MUSIC_ERROR = MarkerFactory.getMarker("WYNK_MUSIC_ERROR");
    Marker MIGRATION_ERROR = MarkerFactory.getMarker("MIGRATION_ERROR");
    Marker INVALID_ENCRYPTED_MSISDN = MarkerFactory.getMarker("INVALID_ENCRYPTED_MSISDN");
    Marker USER_TARGETING_ERROR = MarkerFactory.getMarker("USER_TARGETING_ERROR");
    Marker AMAZON_SERVICE_ERROR = MarkerFactory.getMarker("AMAZON_SERVICE_ERROR");
    Marker SDK_CLIENT_ERROR = MarkerFactory.getMarker("SDK_CLIENT_ERROR");
    Marker ACTIVE_SUBSCRIPTION_DETAILS = MarkerFactory.getMarker("ACTIVE_SUBSCRIPTION_DETAILS");
    Marker PRODUCT_COMPUTATION_ERROR = MarkerFactory.getMarker("PRODUCT_COMPUTATION_ERROR");

}
