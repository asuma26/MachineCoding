package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AnalysedEntity
public class PartnerMeta {

    @Analysed
    @JsonProperty("callback_endpoint")
    private final String callbackEndpoint;
    @Analysed
    @JsonProperty("invoice_eligible")
    private final boolean invoiceEligible;
    @Analysed
    @JsonProperty("state")
    private final String state;

    @JsonCreator
    public PartnerMeta(@JsonProperty("callback_endpoint") String callbackEndpoint, @JsonProperty("invoice_eligible") boolean invoiceEligible, @JsonProperty("state") String state) {
        this.callbackEndpoint = callbackEndpoint;
        this.invoiceEligible = invoiceEligible;
        this.state = state;
    }
}
