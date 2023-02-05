package in.wynk.subscription.dto.request;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AnalysedEntity
public class AirtelEventRequest {

    @Setter
    @Analysed
    private String si;
    @Analysed
    private String col;

}
