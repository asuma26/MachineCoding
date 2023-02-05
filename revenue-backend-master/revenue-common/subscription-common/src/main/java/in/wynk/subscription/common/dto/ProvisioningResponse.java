package in.wynk.subscription.common.dto;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AnalysedEntity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ProvisioningResponse {

}
