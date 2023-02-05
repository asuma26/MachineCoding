package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.dto.SessionDTO;
import in.wynk.session.aspect.advice.ManageSession;
import in.wynk.session.context.SessionContextHolder;
import in.wynk.subscription.common.dto.PlanProvisioningResponse;
import in.wynk.subscription.common.dto.PlanUnProvisioningRequest;
import in.wynk.subscription.service.IUserPlanManager;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wynk/v1/plan")
public class PlanSubscriptionController {

    private final IUserPlanManager planManager;

    public PlanSubscriptionController(IUserPlanManager planManager) {
        this.planManager = planManager;
    }

    @ManageSession(sessionId = "#sid")
    @PostMapping(value = "/unsubscribe/{sid}")
    @AnalyseTransaction(name = "unsubscribePlan")
    public PlanProvisioningResponse unsubscribePlan(@PathVariable String sid, @RequestBody PlanUnProvisioningRequest request) {
        AnalyticService.update(request);
        PlanProvisioningResponse response = planManager.unsubscribe(request);
        AnalyticService.update(response);
        return response;
    }

    @ManageSession(sessionId = "#sid")
    @PatchMapping(value = "/unsubscribe/{sid}")
    @AnalyseTransaction(name = "unsubscribePlan")
    public PlanProvisioningResponse unsubscribePlan(@PathVariable String sid, @RequestParam String planId) {
        SessionDTO sessionDTO = SessionContextHolder.getBody();
        String uid = sessionDTO.get(BaseConstants.UID);
        PlanUnProvisioningRequest request = PlanUnProvisioningRequest.builder().planId(NumberUtils.toInt(planId)).uid(uid).paymentPartner(BaseConstants.WYNK).referenceId(sid).build();
        AnalyticService.update(request);
        PlanProvisioningResponse response = planManager.unsubscribe(request);
        AnalyticService.update(response);
        return response;
    }

}
