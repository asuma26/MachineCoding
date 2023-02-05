package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.dto.SessionDTO;
import in.wynk.common.dto.WynkResponse;
import in.wynk.session.aspect.advice.ManageSession;
import in.wynk.session.context.SessionContextHolder;
import in.wynk.subscription.dto.response.UserActiveBenefits;
import in.wynk.subscription.dto.response.UserCombinedBenefits;
import in.wynk.subscription.dto.response.UserEligibleBenefits;
import in.wynk.subscription.service.IUserPlansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static in.wynk.common.constant.BaseConstants.UID;

@RestController
@RequestMapping("/wynk/v1/plans")
public class UserPlanController {

    @Autowired
    private IUserPlansService userPlansService;

    @ManageSession(sessionId = "#sid")
    @GetMapping("/eligiblePlans/{sid}")
    @AnalyseTransaction(name = "eligiblePlans")
    public UserEligibleBenefits getEligiblePlans(@PathVariable String sid) {
        SessionDTO sessionDTO = SessionContextHolder.getBody();
        UserEligibleBenefits.UserEligibleBenefitsData benefits = userPlansService.getUserEligiblePlans(sessionDTO);
        AnalyticService.update(benefits);
        return UserEligibleBenefits.builder().data(benefits).build();
    }

    @ManageSession(sessionId = "#sid")
    @GetMapping("/activePlans/{sid}")
    @AnalyseTransaction(name = "activePlans")
    public UserActiveBenefits getActivePlans(@PathVariable String sid) {
        SessionDTO sessionDTO = SessionContextHolder.getBody();
        UserActiveBenefits benefits = userPlansService.getUserActiveBenefits(sessionDTO);
        AnalyticService.update(benefits);
        return benefits;
    }


    @ManageSession(sessionId = "#sid")
    @GetMapping("/combinedPlans/{sid}")
    @AnalyseTransaction(name = "combinedPlans")
    public WynkResponse<UserCombinedBenefits> getUserCombinedPlans(@PathVariable String sid) {
        SessionDTO sessionDTO = SessionContextHolder.getBody();
        String uid = sessionDTO.get(UID);
        AnalyticService.update(UID, uid);
        WynkResponse<UserCombinedBenefits> benefits = WynkResponse.<UserCombinedBenefits>builder().body(userPlansService.getUserCombinedBenefits(sessionDTO)).build();
        AnalyticService.update(benefits);
        return benefits;
    }

}
