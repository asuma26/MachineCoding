package in.wynk.subscription.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientEventDetails {

    private String uid;
    private String msisdn;
    private String event;
    private int planId;
    private long validTillDate;
    private boolean autoRenewal;
    private String service;
}
