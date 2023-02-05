package in.wynk.subscription.controller;


import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import in.wynk.common.dto.EmptyResponse;
import in.wynk.subscription.scheduler.SubscriptionMongoDumpScheduler;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wynk/v1/scheduler")
@Profile("!prod")
public class SubscriptionMongoDumpController {

    private final SubscriptionMongoDumpScheduler subscriptionMongoDumpScheduler;

    public SubscriptionMongoDumpController(SubscriptionMongoDumpScheduler subscriptionMongoDumpScheduler) {
        this.subscriptionMongoDumpScheduler = subscriptionMongoDumpScheduler;
    }

    @GetMapping("/start/mongoS3Dump")
    @AnalyseTransaction(name = "subscriptionMongoDump")
    public EmptyResponse exportMongoDump() {
        subscriptionMongoDumpScheduler.exportMongoDump();
        return EmptyResponse.response();
    }
}
