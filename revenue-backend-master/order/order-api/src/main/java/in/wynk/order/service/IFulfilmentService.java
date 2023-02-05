package in.wynk.order.service;

import in.wynk.client.context.ClientContext;
import in.wynk.client.core.constant.ClientErrorType;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.common.constant.BaseConstants;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.constant.OrderConstant;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.dto.request.OrderFulfilmentFallbackRequest;
import in.wynk.order.dto.request.OrderFulfilmentRequest;
import in.wynk.order.dto.response.OrderFulfilmentResponse;
import org.apache.commons.lang3.StringUtils;

public interface IFulfilmentService {

    default OrderFulfilmentResponse fulfil(OrderFulfilmentRequest request) {
        ClientDetails clientDetails = (ClientDetails) ClientContext.getClient().orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001));
        Order order = OrderContext.getOrder().orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD003));
        if (StringUtils.isNotEmpty(request.getCallbackUrl())) {
            order.putMeta(BaseConstants.CALLBACK_URL, request.getCallbackUrl());
        }
        if (request instanceof OrderFulfilmentFallbackRequest) {
            order.putMeta(OrderConstant.IS_PRE_FULFILLED, ((OrderFulfilmentFallbackRequest) request).isPreFulfilled());
        }
        switch (request.getExecutionType()) {
            case SYNC:
                boolean isAsyncSupported = clientDetails.<Boolean>getMeta(BaseConstants.CLIENT_ASYNC_SUPPORTED).orElse(false);
                return fulfilSync(isAsyncSupported, clientDetails, request);
            case ASYNC:
                return fulfilAsync(clientDetails, request);
            default:
                throw new WynkRuntimeException("unknown execution type");
        }
    }

    OrderFulfilmentResponse fulfilSync(boolean isAsyncSupported, ClientDetails clientDetails, OrderFulfilmentRequest request);

    OrderFulfilmentResponse fulfilAsync(ClientDetails clientDetails, OrderFulfilmentRequest request);

}
