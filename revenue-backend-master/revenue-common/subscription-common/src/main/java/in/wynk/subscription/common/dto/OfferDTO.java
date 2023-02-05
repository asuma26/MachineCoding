package in.wynk.subscription.common.dto;

import in.wynk.subscription.common.enums.ProvisionType;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OfferDTO {
    private Integer id;
    private String description;
    private String packGroup;
    private List<Integer> plans;
    private ProvisionType provisionType;
    private String service;
    private String title;
    private Map<String, String> products;
    private boolean isCombo;
}
