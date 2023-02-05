package in.wynk.order.exception;

import in.wynk.exception.IWynkErrorType;
import in.wynk.exception.WynkRuntimeException;
import org.slf4j.Marker;

public class OrderFallbackException extends WynkRuntimeException {

    public OrderFallbackException(Marker marker, String message, Throwable th) {
        super(marker, message, th);
    }

    public OrderFallbackException(IWynkErrorType wynkErrorType) {
        super(wynkErrorType);
    }

    public OrderFallbackException(IWynkErrorType wynkErrorType, Marker marker) {
        super(wynkErrorType, marker);
    }

    public OrderFallbackException(IWynkErrorType wynkErrorType, String message) {
        super(wynkErrorType, message);
    }

    public OrderFallbackException(IWynkErrorType wynkErrorType, Throwable th, String message) {
        super(wynkErrorType, th, message);
    }

    public OrderFallbackException(IWynkErrorType wynkErrorType, Throwable th) {
        super(wynkErrorType, th);
    }

    public OrderFallbackException(String message) {
        super(message);
    }

    public OrderFallbackException(String errorCode, String message, String errorTitle, Throwable th) {
        super(errorCode, message, errorTitle, th);
    }

    public OrderFallbackException(String errorCode, String message, String errorTitle) {
        super(errorCode, message, errorTitle);
    }

    public OrderFallbackException(Throwable th) {
        super(th);
    }

    public OrderFallbackException(String errorMsg, Throwable th) {
        super(errorMsg, th);
    }

}
