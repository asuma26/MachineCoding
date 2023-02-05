package in.wynk.subscription.common.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AllPlansResponse {

    private List<PlanDTO> plans;

}
