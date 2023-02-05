package in.wynk.subscription.common.dto;

import lombok.*;

import java.util.concurrent.TimeUnit;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PlanPeriodDTO {

    private int validity;
    private int retryInterval;
    private int maxRetryCount;
    private String validityUnit;
    private TimeUnit timeUnit;

}
