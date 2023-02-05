package in.wynk.order.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.dto.FreshOrder;
import in.wynk.order.common.dto.PartnerOrderDetails;
import in.wynk.order.common.dto.PartnerPaymentDetails;
import in.wynk.order.constant.OrderConstant;
import in.wynk.order.service.Mapper;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AnalysedEntity
public class OrderAirtelRequest implements Mapper<OrderPlacementRequest> {

    @Analysed
    @JsonProperty("airtel_unique_id")
    private final String airtelUniqueId;
    @Analysed
    @JsonProperty("product_key")
    private final String productKey;
    @Analysed
    @JsonProperty("merchant_key")
    private final String merchantKey;
    @Analysed
    @JsonProperty("start_date")
    private final long startDate;
    @Analysed
    @JsonProperty("user_details")
    private final UserDetails userDetails;
    @Analysed
    @JsonProperty("partner_meta")
    private final PartnerMeta partnerMeta;

    @JsonCreator
    public OrderAirtelRequest(@JsonProperty("airtel_unique_id") String airtelUniqueId, @JsonProperty("product_key") String productKey, @JsonProperty("merchant_key") String merchantKey, @JsonProperty("start_date") long startDate, @JsonProperty("user_details") UserDetails userDetails, @JsonProperty("partner_meta") PartnerMeta partnerMeta) {
        this.airtelUniqueId = airtelUniqueId;
        this.productKey = productKey;
        this.merchantKey = merchantKey;
        this.startDate = startDate;
        this.userDetails = userDetails;
        this.partnerMeta = partnerMeta;
    }

    @Override
    public OrderPlacementRequest convert() {
        return OrderPlacementRequest.builder()
                .callbackUrl(partnerMeta.getCallbackEndpoint())
                .msisdn(userDetails.getUserId())
                .partnerOrder(FreshOrder.builder()
                        .id(airtelUniqueId)
                        .planId(Integer.parseInt(productKey))
                        .paymentDetails(PartnerPaymentDetails.builder()
                                .gatewayName(OrderConstant.AIRTEL_DIGITAL_STORE)
                                .build())
                        .orderDetails(PartnerOrderDetails.builder()
                                .amount(0.0)
                                .currency(OrderConstant.DEFAULT_CURRENCY)
                                .build())
                        .build())
                .build();
    }

}
