package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.dto.SessionDTO;
import in.wynk.common.dto.SessionRequest;
import in.wynk.common.dto.SessionResponse;
import in.wynk.common.dto.WynkResponse;
import in.wynk.partner.common.dto.UserActivePlansResponse;
import in.wynk.session.service.ISessionManager;
import in.wynk.subscription.common.dto.ActivePlanDetails;
import in.wynk.subscription.dto.response.UserCombinedBenefits;
import in.wynk.subscription.dto.response.UserEligibleBenefits;
import in.wynk.subscription.service.ISubscriptionSessionService;
import in.wynk.subscription.service.IUserPlansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wynk/s2s/v1/plans")
public class UserPlanS2SController {

    @Autowired
    private IUserPlansService userPlansService;
    @Autowired
    private ISessionManager sessionManager;

    @Autowired
    private ISubscriptionSessionService sessionService;

    @Value("${session.duration:15}")
    private Integer duration;

    @PostMapping("/eligible")
    @AnalyseTransaction(name = "eligiblePlans")
    public UserEligibleBenefits getEligiblePlans(@RequestBody SessionRequest request) {
        AnalyticService.update(request);
        SessionResponse sessionResponse = sessionService.managePlans(request);
        SessionDTO sessionDTO = (SessionDTO)sessionManager.get(UUID.fromString(sessionResponse.getData().getSid())).getBody();
        UserEligibleBenefits.UserEligibleBenefitsData benefits = userPlansService.getUserEligiblePlans(sessionDTO);
        benefits.setSid(sessionResponse.getData().getSid());
        benefits.setRedirectUrl(sessionResponse.getData().getRedirectUrl());
        AnalyticService.update(benefits);
        return UserEligibleBenefits.builder().data(benefits).build();
    }

    @PostMapping("/combine")
    @AnalyseTransaction(name = "combinedPlans")
    public WynkResponse<UserCombinedBenefits> getUserCombinedPlans(@RequestBody SessionRequest request) {
        AnalyticService.update(request);
        AnalyticService.update(request);
        SessionResponse sessionResponse = sessionService.managePlans(request);
        SessionDTO sessionDTO = (SessionDTO)sessionManager.get(UUID.fromString(sessionResponse.getData().getSid())).getBody();
        UserCombinedBenefits userCombinedBenefits = userPlansService.getUserCombinedBenefits(sessionDTO);
        userCombinedBenefits.setSid(sessionResponse.getData().getSid());
        userCombinedBenefits.setRedirectUrl(sessionResponse.getData().getRedirectUrl());
        WynkResponse<UserCombinedBenefits> benefits = WynkResponse.<UserCombinedBenefits>builder().body(userCombinedBenefits).build();
        AnalyticService.update(benefits);
        return benefits;
    }

    @GetMapping(value = "/active")
    public UserActivePlansResponse getActivePlan(@RequestParam String uid, @RequestParam String service) {
        return userPlansService.getActivePlansForUser(uid, service);
    }

    @GetMapping(value = "/active/details")
    public WynkResponse<ActivePlanDetails> getActivePlanDetails(@RequestParam String uid, @RequestParam int planId) {
        return WynkResponse.<ActivePlanDetails>builder().body(userPlansService.getActivePlanDetails(uid, planId)).build();
    }

}
