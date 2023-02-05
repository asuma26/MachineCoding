package in.wynk.order.common.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AnalysedEntity
@RequiredArgsConstructor
public enum OrderStatusDetail {

    /* Success Order Codes */
    ORDER_SUC200("200", "Order is provisionally acknowledged"),
    ORDER_SUC201("201", "Order is acknowledged"),
    ORDER_SUC202("202", "Order is provisionally fulfilled"),
    ORDER_SUC203("203", "Order is fulfilled"),
    ORDER_SUC204("204", "Order is acknowledged and under fulfilment stage"),
    ORDER_SUC205("205", "Order is deferred"),
    ORDER_SUC206("206", "Order is already fulfilled"),
    ORDER_SUC207("206", "Order is deferred but under fulfilment stage"),

    /* Failure Order Codes */
    ORDER_FAIL400("400", "Order is not acknowledged and failed"),
    ORDER_FAIL401("401", "Order is not fulfilled and failed"),
    ORDER_FAIL402("402", "Order is not notified and failed"),
    ORDER_FAIL403("403", "Order is failed to be fulfilled"),
    ORDER_FAIL404("404", "Order status is unknown");

    @Analysed
    private final String code;
    @Analysed
    private final String description;

}
