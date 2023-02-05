package in.wynk.subscription.dto;

import in.wynk.common.enums.WynkService;
import in.wynk.subscription.core.dao.entity.Offer;
import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.subscription.core.dao.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Abhishek
 * @created 26/06/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductProvisionRequest {
    private Product product;
    private Offer offer;
    private WynkService service;
    private String uid;
    private String msisdn;
    private Plan plan;
    private String deviceId;
    private String transactionId;
    private boolean autoRenewOff;
}
