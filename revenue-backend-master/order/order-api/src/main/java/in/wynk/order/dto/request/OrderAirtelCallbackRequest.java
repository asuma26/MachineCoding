package in.wynk.order.dto.request;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.order.common.enums.OrderStatus;
import in.wynk.order.constant.OrderConstant;
import in.wynk.order.dto.response.OrderNotificationResponse;
import in.wynk.order.mapper.IOrderCallbackMapper;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Getter
@ToString
@AnalysedEntity
@Scope(value = "prototype")
@Component(value = OrderConstant.AIRTEL_DIGITAL_STORE)
public class OrderAirtelCallbackRequest implements IOrderCallbackMapper<OrderAirtelCallbackRequest, OrderNotificationResponse> {

    @Analysed
    private String airtelUniqueId;
    @Analysed
    private String partnerId;
    @Analysed
    private String status;

    @Analysed
    private long dateLastUpdated;
    @Analysed
    private long startDate;
    @Analysed
    private long endDate;

    @Override
    public OrderAirtelCallbackRequest from(OrderNotificationResponse response) {
        this.airtelUniqueId = response.getPartnerOrderId();
        dateLastUpdated = System.currentTimeMillis();
        this.partnerId = response.getOrderId();
        status = OrderConstant.FAILED;
        if (EnumSet.of(OrderStatus.DEFERRED, OrderStatus.FULFILLED).contains(response.getOrderDetails().getStatus())) {
            startDate = response.getPlanDetails().getStartDate();
            endDate = response.getPlanDetails().getEndDate();
            status = OrderConstant.ACTIVE;
        }
        return this;
    }
}
