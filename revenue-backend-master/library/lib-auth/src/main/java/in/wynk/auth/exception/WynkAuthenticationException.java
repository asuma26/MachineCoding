package in.wynk.auth.exception;

import in.wynk.exception.IWynkErrorType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.springframework.security.core.AuthenticationException;

public class WynkAuthenticationException extends AuthenticationException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9013187864093076005L;

    /** The error code. */
    private String            errorCode;

    /** The error title. */
    private String            errorTitle;

    private Marker marker;

    public WynkAuthenticationException(Marker marker, String message, Throwable th) {
        super(message + " ERROR: " + th.getMessage(), th);
        this.marker = marker;
    }

    public WynkAuthenticationException(IWynkErrorType wynkErrorType) {
        super(wynkErrorType.getErrorMessage());
        this.errorTitle = wynkErrorType.getErrorTitle();
        this.errorCode = wynkErrorType.getErrorCode();
        this.marker = wynkErrorType.getMarker();
    }

    public WynkAuthenticationException(IWynkErrorType wynkErrorType, String message) {
        super(formErrorMessage(wynkErrorType, message));
        this.errorTitle = wynkErrorType.getErrorTitle();
        this.errorCode = wynkErrorType.getErrorCode();
    }

    public WynkAuthenticationException(IWynkErrorType wynkErrorType, Throwable th, String message) {
        super(formErrorMessage(wynkErrorType, message), th);
        this.errorTitle = wynkErrorType.getErrorTitle();
        this.errorCode = wynkErrorType.getErrorCode();
    }

    private static String formErrorMessage(IWynkErrorType wynkErrorType, String message) {
        String errorMsg = wynkErrorType.getErrorMessage();
        if(StringUtils.isNotBlank(message)) {
            errorMsg += " - " + message;
        }
        return errorMsg;
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param wynkErrorType
     *            the wynk error type
     * @param th
     *            the throwable
     */
    public WynkAuthenticationException(IWynkErrorType wynkErrorType, Throwable th) {
        super(wynkErrorType.getErrorMessage(), th);
        this.errorTitle = wynkErrorType.getErrorTitle();
        this.errorCode = wynkErrorType.getErrorCode();
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param message
     *            the message
     */
    public WynkAuthenticationException(String message) {
        super(message);
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param errorCode
     *            the error code
     * @param message
     *            the message
     * @param errorTitle
     *            the error title
     * @param th
     *            the throwable
     */
    public WynkAuthenticationException(String errorCode, String message, String errorTitle, Throwable th) {
        super(message, th);
        this.errorTitle = errorTitle;
        this.errorCode = errorCode;
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param errorCode
     *            the error code
     * @param message
     *            the message
     * @param errorTitle
     *            the error title
     */
    public WynkAuthenticationException(String errorCode, String message, String errorTitle) {
        super(message);
        this.errorTitle = errorTitle;
        this.errorCode = errorCode;
    }

    /**
     * Instantiates a new portal exception.
     *
     * @param errorMsg
     *            the error msg
     * @param th
     *            the throwable
     */
    public WynkAuthenticationException(String errorMsg, Throwable th) {
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
     * @param errorCode
     *            the new error code
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
     * @param errorTitle
     *            the new error title
     */
    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public Marker getMarker() {
        return marker;
    }

}
