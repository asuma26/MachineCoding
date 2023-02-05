package in.wynk.exception;

import org.slf4j.Marker;
import org.springframework.http.HttpStatus;

public interface IWynkErrorType {

    String getErrorCode();

    String getErrorTitle();

    String getErrorMessage();

    HttpStatus getHttpResponseStatusCode();

    Marker getMarker();
}
