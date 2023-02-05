package in.wynk.subscription.enums;

/**
 * @author Abhishek
 * @created 18/06/20
 */
public enum OfferEligibilityStatusReason {
    ALREADY_ACTIVE,
    ACTIVE_PLAN_NOT_FOUND,
    MSISDN_REQUIRED,
    IS_NON_SRILANKA_USER,
    IS_AIRTEL_USER,
    IS_AIRTEL_SRILANKA_USER,
    IS_NON_AIRTEL_USER,
    IS_NON_AIRTEL_SRILANKA_USER,
    THANKS_USER_SEGMENT_FOUND,
    THANKS_USER_SEGMENT_NOT_FOUND,
    THANKS_USER_SEGMENT_NOT_ELIGIBLE,
    AVAILED_FOR_USER,
    AVAILED_LINKED_OFFER,
    NOT_AVAILED_LINKED_OFFER,
    AVAILED_FOR_DEVICE,
    INTERNAL_ERROR,
    NOT_ELIGIBLE_FOR_BUILD_NO,
    DEVICE_ID_REQUIRED,
    NOT_ELIGIBLE_FOR_APP_ID,
    APP_ID_REQUIRED,
    OFFER_NOT_ELIGIBLE_FOR_USERTYPE,
    OS_REQUIRED,
    THANKS_COHORT_NOT_FOUND,
    THANKS_COHORT_NOT_ELIGIBLE,
    ALL_PRODUCTS_ALREADY_PROVISIONED,
    USER_NOT_IN_COLLECTION_PROVIDED,
    COLLECTION_NAME_REQUIRED,
    OFFER_ELIGIBLE_FOR_NOT_IN_USER_TYPE
}