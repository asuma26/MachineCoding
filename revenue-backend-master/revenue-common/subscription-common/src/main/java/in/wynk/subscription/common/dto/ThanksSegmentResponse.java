package in.wynk.subscription.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThanksSegmentResponse {
    @Analysed
    private Map<String, List<String>> segments;
}
