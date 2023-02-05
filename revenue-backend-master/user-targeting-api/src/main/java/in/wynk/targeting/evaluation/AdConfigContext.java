package in.wynk.targeting.evaluation;

import in.wynk.subscription.common.dto.UserProfile;
import in.wynk.targeting.core.dao.entity.mongo.music.UserConfig;
import in.wynk.targeting.core.dao.entity.mongo.persona.UserPersona;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AdConfigContext {

    private final UserPersona userPersona;

    private final UserConfig userConfig;

    private final UserProfile userProfile;
}
