package in.wynk.subscription.core.dao.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.concurrent.TimeUnit;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Period {

    private int grace;
    private int validity;
    private int suspension;
    private int preReminder;
    private int retryInterval;
    private long endDate;
    private Integer month;
    private TimeUnit timeUnit;
    private String validityUnit;

    public int getMaxRetryCount() {
        return grace / retryInterval;
    }

}
