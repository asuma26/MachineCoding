package in.wynk.order.mapper;

import in.wynk.order.dto.response.OrderResponse;

public interface IOrderCallbackMapper<R, T extends OrderResponse> {

    R from(T item);

}
