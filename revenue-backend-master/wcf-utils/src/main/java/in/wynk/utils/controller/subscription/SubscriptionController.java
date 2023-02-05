package in.wynk.utils.controller.subscription;


import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.utils.response.SubscriptionAndUserPlanResponse;
import in.wynk.utils.service.subscription.ISubscriptionUserPlanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/wynk/s2s/v1/subscription")
public class SubscriptionController {


    private final ISubscriptionUserPlanService subscriptionUserPlanService;

    public SubscriptionController(ISubscriptionUserPlanService subscriptionUserPlanService) {
        this.subscriptionUserPlanService = subscriptionUserPlanService;
    }

    @GetMapping("/userplan/details")
    @AnalyseTransaction(name = "userPlanSubscriptionDetails")
    public SubscriptionAndUserPlanResponse getUserPlanSubscription(@RequestParam String service, @RequestParam String msisdn) {
        AnalyticService.update(BaseConstants.SERVICE, service);
        AnalyticService.update(BaseConstants.MSISDN, service);
        String uid = MsisdnUtils.getUidFromMsisdn(msisdn);
        SubscriptionAndUserPlanResponse subscriptionAndUserPlanResponse = subscriptionUserPlanService.populateResponse(uid,service);
        AnalyticService.update(subscriptionAndUserPlanResponse);
        return subscriptionAndUserPlanResponse;
    }
}
