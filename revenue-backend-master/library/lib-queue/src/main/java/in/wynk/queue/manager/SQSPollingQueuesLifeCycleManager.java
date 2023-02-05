package in.wynk.queue.manager;

import in.wynk.queue.registry.IPollingQueueRegistryManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import javax.annotation.PreDestroy;

@RequiredArgsConstructor
public class SQSPollingQueuesLifeCycleManager implements ApplicationListener<ApplicationReadyEvent> {

    private final IPollingQueueRegistryManager pollingQueueRegistryManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        pollingQueueRegistryManager.startAll();
    }

    @PreDestroy
    public void destroy() {
        pollingQueueRegistryManager.stopAll();
    }

}
