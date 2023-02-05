package in.wynk.subscription.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
public class SubscriptionMongoDumpScheduler {

    @Autowired
    private SubscriptionMongoDumpService subscriptionMongoDumpService;

    @Autowired
    private ExecutorService executorService;

    @Scheduled(fixedDelay = 60 * 60 * 1000, initialDelay = 60 * 60 * 1000)
    public void exportMongoDump() {
        executorService.submit(() -> subscriptionMongoDumpService.startMongoS3Export());
    }

}
