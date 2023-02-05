package in.wynk.order.service.impl;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.auth.dao.entity.Client;
import in.wynk.client.context.ClientContext;
import in.wynk.client.core.constant.ClientLoggingMarker;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.utils.BeanLocatorFactory;
import in.wynk.common.utils.ChecksumUtils;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.common.dto.WynkPlanDetails;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.constant.BeanConstant;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.constant.OrderLoggingMarker;
import in.wynk.order.core.dao.entity.DeferredOrderDetail;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.dto.request.OrderNotificationRequest;
import in.wynk.order.dto.response.OrderNotificationResponse;
import in.wynk.order.mapper.IOrderCallbackMapper;
import in.wynk.order.service.IDeferredService;
import in.wynk.order.service.INotificationService;
import in.wynk.order.service.ISubscriptionClientService;
import in.wynk.subscription.common.dto.ActivePlanDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
public class OrderNotificationServiceImpl implements INotificationService {

    private final RestTemplate restTemplate;
    private final IDeferredService deferredService;
    private final ISubscriptionClientService subscriptionClientService;

    public OrderNotificationServiceImpl(@Qualifier(BeanConstant.PARTNER_S2S_NOTIFIER_TEMPLATE) RestTemplate restTemplate, IDeferredService deferredService, ISubscriptionClientService subscriptionClientService) {
        this.restTemplate = restTemplate;
        this.deferredService = deferredService;
        this.subscriptionClientService = subscriptionClientService;
    }

    @Override
    public OrderNotificationResponse buildNotificationResponse(OrderNotificationRequest request) {
        Order order = OrderContext.getOrder().orElseThrow(() -> new WynkRuntimeException(OrderErrorType.ORD003));
        OrderNotificationResponse.OrderNotificationResponseBuilder builder = OrderNotificationResponse.builder()
                .partnerOrderId(order.getPartnerOrderId())
                .orderDetails(request.getOrderDetails())
                .orderId(request.getOrderId())
                .msisdn(request.getMsisdn());
        if (request.getPlanDetails() != null) {
            return builder.planDetails(request.getPlanDetails()).build();
        } else {
            switch (request.getOrderDetails().getStatus()) {
                case FULFILLED:
                    ActivePlanDetails planDetails = subscriptionClientService.getActivePlan(order.getUid(), order.getPlanId());
                    builder.planDetails(WynkPlanDetails.builder()
                            .startDate(planDetails.getValidFromDate().getTime())
                            .endDate(planDetails.getValidTillDate().getTime())
                            .autoRenew(planDetails.isAutoRenew())
                            .planId(order.getPlanId())
                            .build());
                    break;
                case DEFERRED:
                    DeferredOrderDetail deferredOrderDetail = deferredService.getDeferredOrderDetails(request.getOrderId());
                    builder.planDetails(WynkPlanDetails.builder()
                            .startDate(deferredOrderDetail.getUntilDate().getTimeInMillis())
                            .endDate(deferredOrderDetail.getValidUntil().getTimeInMillis())
                            .planId(order.getPlanId())
                            .build());
                    break;
                case FAILED:
                case UNKNOWN:
                    builder.planDetails(WynkPlanDetails.builder().planId(order.getPlanId()).build());
                    break;
            }
            return builder.build();
        }
    }

    @Override
    public OrderNotificationResponse notifyAsync(OrderNotificationRequest request) {
        OrderNotificationResponse orderNotificationResponse = null;
        try {
            Optional<Client> optional = ClientContext.getClient();
            if (optional.isPresent()) {
                ClientDetails clientDetails = (ClientDetails) optional.get();
                log.info("order notification request is received for partner: {} and order id: {}", clientDetails.getAlias(), request.getOrderId());
                Optional<Boolean> callbackEnabledOption = clientDetails.getMeta(BaseConstants.CALLBACK_ENABLED);
                if (callbackEnabledOption.isPresent() && callbackEnabledOption.get()) {
                    Optional<String> callbackUrlOption = clientDetails.getMeta(BaseConstants.CALLBACK_URL);
                    if (callbackUrlOption.isPresent() || StringUtils.isNotEmpty(request.getCallbackUrl())) {
                        orderNotificationResponse = buildNotificationResponse(request);
                        String callbackUrl = Optional.ofNullable(request.getCallbackUrl()).orElseGet(callbackUrlOption::get);
                        IOrderCallbackMapper requestMapper = BeanLocatorFactory.getBeanOrDefault(clientDetails.getAlias(), IOrderCallbackMapper.class, orderNotificationResponse);
                        RequestEntity<Object> requestHttpEntity = ChecksumUtils.buildEntityWithChecksum(callbackUrl, clientDetails.getClientId(), clientDetails.getClientSecret(), requestMapper.from(orderNotificationResponse), HttpMethod.POST);
                        AnalyticService.update(BaseConstants.CLIENT_REQUEST, requestHttpEntity.toString());
                        try {
                            ResponseEntity<String> partnerResponse = restTemplate.exchange(requestHttpEntity, String.class);
                            AnalyticService.update(BaseConstants.CLIENT_RESPONSE, partnerResponse.toString());
                        } catch (HttpStatusCodeException e) {
                            AnalyticService.update(BaseConstants.CLIENT_RESPONSE, e.getResponseBodyAsString());
                            throw new WynkRuntimeException(OrderErrorType.ORD007, e);
                        } catch (Exception e) {
                            log.error(ClientLoggingMarker.CLIENT_COMMUNICATION_ERROR, e.getMessage() + " for client " + clientDetails.getAlias(), e);
                        }
                    } else {
                        log.warn(OrderLoggingMarker.ORDER_NOTIFICATION_INFO, "callback url is neither registered nor found in request for client: {} and order id: {}", clientDetails.getAlias(), request.getOrderId());
                    }
                }
            } else {
                log.error(OrderLoggingMarker.ORDER_NOTIFICATION_ERROR, "no partner details found to send order notification");
                throw new WynkRuntimeException(OrderErrorType.ORD008);
            }
        } catch (WynkRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new WynkRuntimeException(OrderErrorType.ORD009, e);
        }
        return orderNotificationResponse;
    }


}
