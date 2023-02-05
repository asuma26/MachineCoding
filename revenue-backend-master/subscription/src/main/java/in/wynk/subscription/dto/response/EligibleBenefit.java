package in.wynk.subscription.dto.response;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.subscription.common.dto.PartnerDTO;
import in.wynk.subscription.core.dao.entity.DescriptionObject;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AnalysedEntity
public class EligibleBenefit {

    @Analysed
    private final int id;
    private final boolean combo;
    private final String packGroup;
    private final String partnerName;
    private final Map<String, List<String>> partnerContentImages;
    private final List<DescriptionObject> descriptions;
    private final String partnerLogo;
    private final String partnerIcon;
    private final String colorCode;
    private final String planStartTitle;
    private final Integer startingRate;
    private final int hierarchy;
    private final int displayOrder;

    @Analysed
    private final List<AvailablePlans> plans;
    private final List<PartnerDTO> partners;
    private final String button;
    private final String comboHeader;

    @Analysed
    @Setter
    private String title;
    @Setter
    private String subtitle;
    @Setter
    private Map<String, Object> meta;

    public Map<String, Object> getMeta() {
        if (meta == null) {
            return new HashMap<>();
        }
        return meta;
    }

}
