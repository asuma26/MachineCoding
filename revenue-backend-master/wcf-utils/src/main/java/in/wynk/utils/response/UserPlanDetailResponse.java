package in.wynk.utils.response;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@AnalysedEntity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPlanDetailResponse {
    private int planId;
    private String planType;
    private Date startDate;
    private Date endDate;
    @Setter
    private int planPrice = 0;

}
