package in.wynk.payment.dto.phonepe;

public enum PhonePeStatusEnum {
    INVALID_TRANSACTION_ID,
    INVALID_USER_AUTH_TOKEN,
    BAD_REQUEST,
    AUTHORIZATION_FAILED,
    INTERNAL_SERVER_ERROR,
    TRANSACTION_NOT_FOUND,
    PAYMENT_SUCCESS,
    PAYMENT_ERROR,
    PAYMENT_PENDING,
    PAYMENT_DECLINED,
    PAYMENT_CANCELLED,
    EXTERNAL_VPA_ERROR,
    SUCCESS
}
