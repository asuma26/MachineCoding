package in.wynk.utils.response;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@AnalysedEntity
@Getter
public class SubscriptionProductResponse {
    private int productId;
    private Date subscriptionDate;
    private Date validTillDate;
    private String service;
    private Date subscriptionEndDate;
    private Date nextChargingDate;
}
