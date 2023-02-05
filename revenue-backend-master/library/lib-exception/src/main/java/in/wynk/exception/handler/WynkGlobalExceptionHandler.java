package in.wynk.exception.handler;

import in.wynk.exception.WynkRuntimeException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static in.wynk.exception.WynkErrorType.UT999;
import static in.wynk.exception.constants.ExceptionConstants.REQUEST_ID;
import static in.wynk.logging.BaseLoggingMarkers.APPLICATION_ERROR;
import static in.wynk.logging.BaseLoggingMarkers.SPRING_REQUEST_ERROR;

@ControllerAdvice
public class WynkGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(WynkGlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleControllerException(WebRequest req, Exception ex) {
        logger.error(APPLICATION_ERROR, ex.getMessage(), ex);
        try {
            return super.handleException(ex, req);
        } catch (Exception ignored) {
        }
        ExceptionDetails exceptionDetails = new ExceptionDetails();
        exceptionDetails.setError(UT999.getErrorMessage());
        exceptionDetails.setErrorCode(UT999.getErrorCode());
        exceptionDetails.setErrorTitle(UT999.getErrorTitle());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDetails);
    }

    @ExceptionHandler({WynkRuntimeException.class})
    public ResponseEntity<ExceptionDetails> handleWynkRuntimeExceptionInternal(WynkRuntimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex.getErrorType() != null && ex.getErrorType().getHttpResponseStatusCode() != null) {
            status = ex.getErrorType().getHttpResponseStatusCode();
        }
        return new ResponseEntity<>(new ExceptionDetails(ex), status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error(SPRING_REQUEST_ERROR, ex.getMessage(), ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @Data
    private static class ExceptionDetails {
        private String errorCode;
        private String errorTitle;
        private String error;
        private boolean success;
        private String requestId;

        private ExceptionDetails() {
            requestId = MDC.get(REQUEST_ID);
            success = false;
        }

        private ExceptionDetails(WynkRuntimeException ex) {
            requestId = MDC.get(REQUEST_ID);
            success = false;
            this.errorCode = ex.getErrorCode();
            this.errorTitle = ex.getErrorTitle();
            this.error = ex.getMessage();
        }

    }
}
