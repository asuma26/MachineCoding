package in.wynk.order.dto.response;

import in.wynk.common.dto.BaseResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WynkOrderResponse extends BaseResponse<OrderResponse> {

    private OrderResponse data;

}
