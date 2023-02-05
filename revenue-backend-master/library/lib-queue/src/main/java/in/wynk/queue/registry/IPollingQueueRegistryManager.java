package in.wynk.queue.registry;

import in.wynk.queue.poller.ISQSMessagePollingQueue;

public interface IPollingQueueRegistryManager {

     void register(ISQSMessagePollingQueue pollingQueue);

     void startAll();

     void stopAll();

}
