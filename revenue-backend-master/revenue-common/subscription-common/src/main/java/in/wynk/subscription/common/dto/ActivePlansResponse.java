package in.wynk.subscription.common.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivePlansResponse {
    @Analysed
    private List<Integer> planIds;
}
