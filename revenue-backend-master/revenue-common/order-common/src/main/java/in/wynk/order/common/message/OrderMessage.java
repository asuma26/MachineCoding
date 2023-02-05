package in.wynk.order.common.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage {

    @Analysed
    @JsonProperty("order_id")
    private String orderId;
    @Analysed
    @JsonProperty("mobile_no")
    private String msisdn;
    @Analysed
    @JsonProperty("partner_id")
    private String partnerId;
    @Analysed
    @JsonProperty("callback_url")
    private String callbackUrl;
}
