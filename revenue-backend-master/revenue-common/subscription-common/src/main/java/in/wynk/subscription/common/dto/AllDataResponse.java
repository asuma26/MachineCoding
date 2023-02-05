package in.wynk.subscription.common.dto;

import in.wynk.common.dto.BaseResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AllDataResponse extends BaseResponse<Object> {

    private Object data;

    @Override
    public Object getData() {
        return data;
    }

}
