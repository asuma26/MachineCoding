package in.wynk.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;

/**
 * The Class PortalException.
 */
public class WynkRuntimeException extends RuntimeException {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -9013187864093076005L;

    /**
     * The error code.
     */
    private String errorCode;

    /**
     * The error title.
     */
    private String errorTitle;

    @JsonIgnore
    private IWynkErrorType errorType;

    private Marker marker;

    public WynkRuntimeException(Marker marker, String message, Throwable th) {
        super(new StringBuilder(message).append(" ERROR: ").append(th.getMessage()).toString(), th);
        this.marker = marker;
    }

    public WynkRuntimeException(IWynkErrorType wynkErrorType) {
        super(wynkErrorType.getErrorMessage());
        this.errorTitle = wynkErrorType.getErrorTitle();
        this.errorCode = wynkErrorType.getErrorCode();
        this.marker = wynkErrorType.getMarker();
        this.errorType = wynkErrorType;
    }

    public WynkRuntimeException(IWynkErrorType wynkErrorType, Marker marker) {
        super(wynkErrorType.getErrorMessage());
        this.errorTitle = wynkErrorType.getErrorTitle();
        this.errorCode = wynkErrorType.getErrorCode();
        this.marker = marker;
        this.errorType = wynkErrorType;
    }

    public WynkRuntimeException(IWynkErrorType wynkErrorType, String message) {
        super(formErrorMessage(wynkErrorType, message));
        this.errorTitle = wynkErrorType.getErrorTitle();
        this.errorCode = wynkErrorType.getErrorCode();
        this.errorType = wynkErrorType;
    }

    public WynkRuntimeException(IWynkErrorType wynkErrorType, Throwable th, String message) {
        super(formErrorMessage(wynkErrorType, message), th);
        this.errorTitle = wynkErrorType.getErrorTitle();
        this.errorCode = wynkErrorType.getErrorCode();
        this.errorType = wynkErrorType;
    }

    private static String formErrorMessage(IWynkErrorType wynkErrorType, String message) {
        String errorMsg = wynkErrorType.getErrorMessage();
        if (StringUtils.isNotBlank(message)) {
            errorMsg += " - " + message;
        }
        return errorMsg;
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param wynkErrorType the wynk error type
     * @param th            the throwable
     */
    public WynkRuntimeException(IWynkErrorType wynkErrorType, Throwable th) {
        super(wynkErrorType.getErrorMessage(), th);
        this.errorTitle = wynkErrorType.getErrorTitle();
        this.errorCode = wynkErrorType.getErrorCode();
        this.errorType = wynkErrorType;
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param message the message
     */
    public WynkRuntimeException(String message) {
        super(message);
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param errorCode  the error code
     * @param message    the message
     * @param errorTitle the error title
     * @param th         the throwable
     */
    public WynkRuntimeException(String errorCode, String message, String errorTitle, Throwable th) {
        super(message, th);
        this.errorTitle = errorTitle;
        this.errorCode = errorCode;
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param errorCode  the error code
     * @param message    the message
     * @param errorTitle the error title
     */
    public WynkRuntimeException(String errorCode, String message, String errorTitle) {
        super(message);
        this.errorTitle = errorTitle;
        this.errorCode = errorCode;
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param th the throwable
     */
    public WynkRuntimeException(Throwable th) {
        super(th.getMessage());
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param errorMsg the error msg
     * @param th       the throwable
     */
    public WynkRuntimeException(String errorMsg, Throwable th) {
        super(errorMsg, th);
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the new error code
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the error title.
     *
     * @return the error title
     */
    public String getErrorTitle() {
        return errorTitle;
    }

    /**
     * Sets the error title.
     *
     * @param errorTitle the new error title
     */
    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public Marker getMarker() {
        return marker;
    }

    public IWynkErrorType getErrorType() {
        return errorType;
    }

}
