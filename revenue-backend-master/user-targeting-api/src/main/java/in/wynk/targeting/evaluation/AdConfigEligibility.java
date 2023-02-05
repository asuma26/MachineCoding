package in.wynk.targeting.evaluation;

import in.wynk.subscription.common.dto.UserProfile;
import in.wynk.targeting.core.dao.entity.mongo.music.UserConfig;
import in.wynk.targeting.core.dao.entity.mongo.persona.UserPersona;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Builder
public class AdConfigEligibility {

    private final AdConfigContext adConfigContext;

    public boolean isUserInAtvSegment(String segment) {
        UserPersona userPersona = adConfigContext.getUserPersona();
        return Optional
                .of(segment)
                .map(_segment -> {
                    if (userPersona != null &&
                            userPersona.getAtv() != null &&
                            userPersona.getAtv().getUser() != null &&
                            userPersona.getAtv().getUser().getCurrSeg() != null) {
                        return _segment.equalsIgnoreCase(userPersona.getAtv().getUser().getCurrSeg());
                    }
                    return false;
                })
                .orElse(false);
    }

    public boolean hasMusicSubscriptionId(String... subscriptionIds) {
        UserConfig userConfig = adConfigContext.getUserConfig();
        if(userConfig != null && userConfig.getAttributes() != null
                && userConfig.getAttributes().getSubscriptionId() != null) {
            List<String> currentSubscriptionIds = userConfig.getAttributes().getSubscriptionId();
            return Arrays.stream(subscriptionIds).anyMatch(currentSubscriptionIds::contains);
        }
        return true;
    }

    public boolean hasNotMusicSubscriptionId(String... subscriptionIds) {
        UserConfig userConfig = adConfigContext.getUserConfig();
        if(userConfig != null && userConfig.getAttributes() != null
                && userConfig.getAttributes().getSubscriptionId() != null) {
            List<String> currentSubscriptionIds = userConfig.getAttributes().getSubscriptionId();
            return Arrays.stream(subscriptionIds).noneMatch(currentSubscriptionIds::contains);
        }
        return true;
    }

    public boolean isUserPlanIn(Integer... planIds) {
        UserProfile userProfile = adConfigContext.getUserProfile();
        if(userProfile != null && userProfile.getActivePlanId() != null) {
            List<Integer> currentPlanIds = userProfile.getActivePlanId();
            return Arrays.stream(planIds).anyMatch(currentPlanIds::contains);
        }
        return true;
    }

    public boolean isUserPlanNotIn(Integer... planIds) {
        UserProfile userProfile = adConfigContext.getUserProfile();
        if(userProfile != null && userProfile.getActivePlanId() != null) {
            List<Integer> currentPlanIds = userProfile.getActivePlanId();
            return Arrays.stream(planIds).noneMatch(currentPlanIds::contains);
        }
        return true;
    }

    public boolean isUserAtvSegmentUnknown() {
        UserPersona userPersona = adConfigContext.getUserPersona();
        return !(userPersona != null &&
                userPersona.getAtv() != null &&
                userPersona.getAtv().getUser() != null &&
                userPersona.getAtv().getUser().getCurrSeg() != null &&
                !StringUtils.isEmpty(userPersona.getAtv().getUser().getCurrSeg()));
    }

}
