package in.wynk.subscription.dto.response;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.subscription.common.dto.PartnerDTO;
import in.wynk.subscription.core.dao.entity.DescriptionObject;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@AnalysedEntity
public class ActiveBenefit {
    @Analysed
    private final int planId;
    @Analysed
    private final long validTill;
    private final String validityUnit;
    private final int displayAmount;
    private final int offerId;
    private final int offerHierarchy;
    private final boolean autoRenew;
    private final String packGroup;
    private final String planType;
    private final String partnerName;
    private final String partnerLogo;
    private final String partnerIcon;
    private final String colorCode;
    private final String title;
    private final String subtitle;
    private final String button;
    private final int total;
    private final List<PartnerDTO> partners;
    private final boolean combo;
    private final List<DescriptionObject> descriptions;

    private final Map<String, Object> meta;

}
