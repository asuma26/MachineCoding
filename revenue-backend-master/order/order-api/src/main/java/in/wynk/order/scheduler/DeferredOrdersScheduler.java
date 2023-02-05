package in.wynk.order.scheduler;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.utils.Utils;
import in.wynk.order.constant.OrderConstant;
import in.wynk.order.core.dao.entity.DeferredOrderDetail;
import in.wynk.order.dto.message.DeferredOrdersMessage;
import in.wynk.order.service.IDeferredService;
import in.wynk.queue.service.ISqsManagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DeferredOrdersScheduler {

    private final ISqsManagerService sqsManagerService;
    private final IDeferredService orderDeferredServiceImpl;

    public DeferredOrdersScheduler(ISqsManagerService sqsManagerService, IDeferredService orderDeferredServiceImpl) {
        this.sqsManagerService = sqsManagerService;
        this.orderDeferredServiceImpl = orderDeferredServiceImpl;
    }

    //@Scheduled(cron = "0 0 * * * ?")
    @AnalyseTransaction(name = "deferredOrdersScheduler")
    @Transactional(readOnly = true)
    public void processDeferredOrders() {
        List<DeferredOrderDetail> deferredOrderDetails;
        try (Stream<DeferredOrderDetail> deferredOrderDetailStream = orderDeferredServiceImpl.getScheduledDeferredOrdersPaginated()
        ) {
            deferredOrderDetails = deferredOrderDetailStream.collect(Collectors.toList());
        }
        if (!deferredOrderDetails.isEmpty()) {
            AnalyticService.update(OrderConstant.SCHEDULED_DEFERRED_ORDERS_IDS, Utils.getGson().toJson(deferredOrderDetails.stream().map(DeferredOrderDetail::getId).collect(Collectors.toList())));
            sendToDeferredQueue(deferredOrderDetails);
        } else {
            AnalyticService.update(OrderConstant.SCHEDULED_DEFERRED_ORDERS_IDS, Utils.getGson().toJson(Collections.EMPTY_LIST));
        }
    }

    public void sendToDeferredQueue(List<DeferredOrderDetail> deferredOrderDetails) {
        for (DeferredOrderDetail deferredOrderDetail : deferredOrderDetails) {
            sqsManagerService.publishSQSMessage(DeferredOrdersMessage.builder()
                    .deferredUntil(deferredOrderDetail.getUntilDate().getTimeInMillis())
                    .validUntil(deferredOrderDetail.getValidUntil().getTimeInMillis())
                    .callbackUrl(deferredOrderDetail.getCallbackUrl())
                    .preFulfilled(deferredOrderDetail.isPreFulfilled())
                    .orderId(deferredOrderDetail.getId())
                    .build());
        }
    }

}
