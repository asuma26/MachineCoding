package in.wynk.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

@Builder
@AnalysedEntity
public class WynkResponse<T> extends ResponseEntity<WynkResponse.WynkResponseWrapper<T>> {

    @Analysed
    private final WynkResponse.WynkResponseWrapper<T> body;
    private final MultiValueMap<String, String> headers;
    @Builder.Default
    private HttpStatus status = HttpStatus.OK;

    public WynkResponse(WynkResponse.WynkResponseWrapper<T> body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers, status);
        this.body = body;
        this.headers = headers;
        this.status = status;
    }

    @Getter
    @SuperBuilder
    @AnalysedEntity
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WynkResponseWrapper<T> extends BaseResponse<T> {
        @Analysed
        private T data;
    }

    public static class WynkResponseBuilder<T> {
        public WynkResponseBuilder<T> body(T data) {
            this.body = WynkResponseWrapper.<T>builder().data(data).build();
            return this;
        }
    }
}
