package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.dto.SessionRequest;
import in.wynk.common.dto.SessionResponse;
import in.wynk.subscription.service.ISubscriptionSessionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wynk/s2s/v1/plans")
public class SessionS2SController {

    @Autowired
    private ISubscriptionSessionService sessionService;

    @ApiOperation("Provides the session Id and the webview URL for purchasing a subscription")
    @PostMapping("/purchase")
    @AnalyseTransaction(name = "purchasePlans")
    public SessionResponse purchasePlans(@RequestBody SessionRequest request) {
        AnalyticService.update(request);
        SessionResponse response = sessionService.purchasePlans(request);
        AnalyticService.update(response);
        return response;
    }

    @ApiOperation("Provides the session Id and the webview URL for managing subscriptions" +
            "\nAn alternate API for old accountPageInit or /bundle/initTxn API")
    @PostMapping("/manage")
    @AnalyseTransaction(name = "managePlans")
    public SessionResponse manageSubscription(@RequestBody SessionRequest request) {
        AnalyticService.update(request);
        SessionResponse response = sessionService.managePlans(request);
        AnalyticService.update(response);
        return response;
    }

}
