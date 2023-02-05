package in.wynk.queue.constant;

import in.wynk.exception.IWynkErrorType;
import org.slf4j.Marker;
import org.springframework.http.HttpStatus;

public enum QueueErrorType implements IWynkErrorType {

    /**
     * SQS Errors
     **/
    SQS001("SQS Producer Failure", "Unable to send SQS message", HttpStatus.INTERNAL_SERVER_ERROR, QueueLoggingMarker.SQS_ERROR),
    SQS002("SQS Producer Failure", "SQS returned error response", HttpStatus.EXPECTATION_FAILED, QueueLoggingMarker.SQS_ERROR),
    SQS003("SQS Consumer Failure", "Unable to receive message from sqs", HttpStatus.INTERNAL_SERVER_ERROR, QueueLoggingMarker.SQS_ERROR),
    SQS004("SQS Consumer Failure", "SQS returned error response", HttpStatus.EXPECTATION_FAILED, QueueLoggingMarker.SQS_ERROR),
    SQS005("SQS Pooling Failure", "Unable to poll sqs queue", HttpStatus.EXPECTATION_FAILED, QueueLoggingMarker.SQS_ERROR),
    SQS006("SQS Producer Failure", "Configuration attributes are not present", HttpStatus.PRECONDITION_FAILED, QueueLoggingMarker.SQS_ERROR),
    SQS007("Invalid Queue Name", "Invalid Queue Name", HttpStatus.PRECONDITION_FAILED, QueueLoggingMarker.SQS_ERROR),
    ;

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
    QueueErrorType(String errorTitle, String errorMsg, HttpStatus httpResponseStatus, Marker marker) {
        this.errorTitle = errorTitle;
        this.errorMsg = errorMsg;
        this.httpResponseStatusCode = httpResponseStatus;
        this.marker = marker;
    }

    @Override
    public String getErrorCode() {
        return null;
    }

    @Override
    public String getErrorTitle() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public HttpStatus getHttpResponseStatusCode() {
        return null;
    }

    @Override
    public Marker getMarker() {
        return null;
    }
}
