package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.dto.WynkResponse;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import in.wynk.subscription.common.dto.PlanUnProvisioningRequest;
import in.wynk.subscription.common.dto.ProvisioningResponse;
import in.wynk.subscription.common.dto.SinglePlanProvisionRequest;
import in.wynk.subscription.service.IUserPlanManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wynk/s2s/v1/plan")
public class PlanSubscriptionS2SController {

    private final IUserPlanManager planManager;

    public PlanSubscriptionS2SController(IUserPlanManager planManager) {
        this.planManager = planManager;
    }

    @PostMapping(value = "/subscribe")
    @AnalyseTransaction(name = "subscribePlan")
    public WynkResponse<ProvisioningResponse> subscribePlan(@RequestBody SinglePlanProvisionRequest request) {
        AnalyticService.update(request);
        WynkResponse<ProvisioningResponse> response = WynkResponse.<ProvisioningResponse>builder().body(planManager.subscribe(request)).build();
        AnalyticService.update(response);
        return response;
    }

    @PostMapping(value = "/unsubscribe")
    @AnalyseTransaction(name = "unsubscribePlan")
    public WynkResponse<PlanProvisioningResponse> unsubscribePlan(@RequestBody PlanUnProvisioningRequest request) {
        AnalyticService.update(request);
        WynkResponse<PlanProvisioningResponse> response = WynkResponse.<PlanProvisioningResponse>builder().body(planManager.unsubscribe(request)).build();
        AnalyticService.update(response);
        return response;
    }

}
