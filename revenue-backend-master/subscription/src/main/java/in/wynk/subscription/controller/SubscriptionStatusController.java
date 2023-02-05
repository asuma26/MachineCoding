package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.dto.WynkResponse;
import in.wynk.subscription.dto.SubscriptionStatus;
import in.wynk.subscription.service.ISubscriptionStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static in.wynk.common.constant.BaseConstants.SERVICE;
import static in.wynk.common.constant.BaseConstants.UID;

@RestController
@RequestMapping("/wynk/s2s/v1/subscription")
public class SubscriptionStatusController {

    private final ISubscriptionStatusService subscriptionStatusService;

    public SubscriptionStatusController(ISubscriptionStatusService subscriptionStatusService) {
        this.subscriptionStatusService = subscriptionStatusService;
    }

    @GetMapping("/status/{uid}/{service}")
    @AnalyseTransaction(name = "subscriptionStatus")
    public WynkResponse<List<SubscriptionStatus>> getSubscriptionStatus(@PathVariable String uid, @PathVariable String service) {
        AnalyticService.update(UID, uid);
        AnalyticService.update(SERVICE, service);
        WynkResponse<List<SubscriptionStatus>> response = WynkResponse.<List<SubscriptionStatus>>builder()
                .body(subscriptionStatusService.getSubscriptionStatus(uid, service)).build();
        AnalyticService.update(response.getBody());
        return response;
    }

}
