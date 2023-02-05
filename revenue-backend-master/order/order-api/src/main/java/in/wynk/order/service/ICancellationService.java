package in.wynk.order.service;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.dto.request.OrderCancellationRequest;
import in.wynk.order.dto.response.OrderCancellationResponse;

public interface ICancellationService {

    default OrderCancellationResponse cancel(String partnerId, OrderCancellationRequest request) {
        switch (request.getExecutionType()) {
            case SYNC:
                return cancelSync(partnerId, request);
            case ASYNC:
                return cancelAsync(partnerId, request);
            default:
                throw new WynkRuntimeException("Unknown execution type");
        }
    }

    OrderCancellationResponse cancelSync(String partnerId, OrderCancellationRequest request);

    OrderCancellationResponse cancelAsync(String partnerId, OrderCancellationRequest request);

}
