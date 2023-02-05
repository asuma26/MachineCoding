package in.wynk.queue.registry;

import in.wynk.queue.poller.ISQSMessagePollingQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class SQSMessagePollingQueueRegistryManager implements IPollingQueueRegistryManager {

    private final Set<ISQSMessagePollingQueue> pollingQueues;

    public SQSMessagePollingQueueRegistryManager() {
        this.pollingQueues = new LinkedHashSet<>();
    }

    public void register(ISQSMessagePollingQueue pollingQueue) {
        this.pollingQueues.add(pollingQueue);
    }

    public void startAll() {
        for (ISQSMessagePollingQueue poller : this.pollingQueues) {
            poller.start();
        }
    }

    public void stopAll() {
        for (ISQSMessagePollingQueue poller : this.pollingQueues) {
            poller.stop();
        }
    }

}
