package in.wynk.order.service;

import in.wynk.client.context.ClientContext;
import in.wynk.client.core.constant.ClientErrorType;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.common.constant.BaseConstants;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.dto.request.OrderPlacementRequest;
import in.wynk.order.dto.response.OrderPlacementResponse;

public interface IAcknowledgementService {

    default OrderPlacementResponse place(OrderPlacementRequest request) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        switch (request.getExecutionType()) {
            case SYNC:
                boolean isAsyncSupported = clientDetails.<Boolean>getMeta(BaseConstants.CLIENT_ASYNC_SUPPORTED).orElse(false);
                return placeSync(isAsyncSupported, clientDetails, request);
            case ASYNC:
                return placeAsync(clientDetails, request);
            default:
                throw new WynkRuntimeException("unknown execution type");
        }
    }

    OrderPlacementResponse placeSync(boolean isPartnerAsyncSupported, ClientDetails clientDetails, OrderPlacementRequest request);

    OrderPlacementResponse placeAsync(ClientDetails clientDetails, OrderPlacementRequest request);

}
