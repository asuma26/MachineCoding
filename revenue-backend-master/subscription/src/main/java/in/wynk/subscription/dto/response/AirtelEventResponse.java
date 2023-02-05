package in.wynk.subscription.dto.response;

import in.wynk.common.dto.BaseResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AirtelEventResponse extends BaseResponse<Void> {
    @Override
    public Void getData() {
        return null;
    }
}
