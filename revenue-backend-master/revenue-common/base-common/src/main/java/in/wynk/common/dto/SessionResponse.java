package in.wynk.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionResponse extends BaseResponse<SessionResponse.SessionData> {

    @Analysed
    private SessionData data;

    @Getter
    @Builder
    @AnalysedEntity
    public static class SessionData {
        @Analysed
        private final String sid;
        @Analysed
        private final String redirectUrl;
    }
}
