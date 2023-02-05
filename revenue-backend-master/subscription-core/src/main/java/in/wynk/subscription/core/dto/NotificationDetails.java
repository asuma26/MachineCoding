package in.wynk.subscription.core.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class NotificationDetails {
    private final String priority;
    private final String message;
    private final String msisdn;
    private final String service;
}
