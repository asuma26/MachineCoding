package in.wynk.subscription.common.dto;

import lombok.*;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartnerDTO {

    private String id;
    private String icon;
    private String logo;
    private String partnerLogo;
    private String partnerIcon;
    private String portrait;
    private String name;
    private String packGroup;
    private String description;
    private String colorCode;
    private Map<String, List<String>> contentImages;

    public String getPartnerLogo() {
        if (StringUtils.isEmpty(partnerLogo)) {
            return this.logo;
        }
        return this.partnerLogo;
    }

    public String getPartnerIcon() {
        if (StringUtils.isEmpty(partnerIcon)) {
            return this.icon;
        }
        return this.partnerLogo;
    }

}
