package in.wynk.client.service;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.auth.dao.entity.Client;
import in.wynk.client.core.constant.ClientLoggingMarker;
import in.wynk.client.core.dao.entity.ClientDetails;
import in.wynk.client.core.dao.repository.IClientDetailsRepository;
import in.wynk.data.enums.State;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static in.wynk.common.constant.BaseConstants.IN_MEMORY_CACHE_CRON;

@Slf4j
@Service
public class ClientDetailsCachingService {


    private final Map<String, Client> CLIENT_BY_IDS_CACHE = new ConcurrentHashMap<>();
    private final Map<String, Client> CLIENT_BY_ALIAS_CACHE = new ConcurrentHashMap<>();
    private final Map<String, Client> CLIENT_BY_SERVICE_CACHE = new ConcurrentHashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();

    private final IClientDetailsRepository clientDetailsRepository;

    public ClientDetailsCachingService(IClientDetailsRepository clientDetailsRepository) {
        this.clientDetailsRepository = clientDetailsRepository;
    }

    @Scheduled(fixedDelay = IN_MEMORY_CACHE_CRON, initialDelay = IN_MEMORY_CACHE_CRON)
    @PostConstruct
    @AnalyseTransaction(name = "refreshInMemoryCache")
    public void init() {
        AnalyticService.update("class", this.getClass().getSimpleName());
        AnalyticService.update("cacheLoadInit", true);
        loadClients();
        AnalyticService.update("cacheLoadCompleted", true);
    }

    public void loadClients() {
        List<Client> clients = clientDetailsRepository.findAll().stream().filter(client -> client.getState() == State.ACTIVE).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(clients) && writeLock.tryLock()) {
            Map<String, Client> clientsAliasMap = new ConcurrentHashMap<>();
            Map<String, Client> clientsIdsMap = new ConcurrentHashMap<>();
            Map<String, Client> serviceClientMap = new ConcurrentHashMap<>();
            try {
                for (Client client : clients) {
                    ClientDetails details = (ClientDetails) client;
                    if (StringUtils.isNotBlank(details.getService())) {
                        serviceClientMap.put(details.getService(), client);
                    }
                    clientsAliasMap.put(details.getAlias(), client);
                    clientsIdsMap.put(details.getClientId(), client);
                }
                CLIENT_BY_IDS_CACHE.putAll(clientsIdsMap);
                CLIENT_BY_SERVICE_CACHE.putAll(serviceClientMap);
                CLIENT_BY_ALIAS_CACHE.putAll(clientsAliasMap);
            } catch (Throwable th) {
                log.error(ClientLoggingMarker.CLIENT_DETAILS_CATCHING_FAILURE, "Exception occurred while refreshing client details cache. Exception: {}", th.getMessage(), th);
                throw th;
            } finally {
                writeLock.unlock();
            }
        }
    }

    public Client getClientById(String clientId) {
        if (StringUtils.isNotEmpty(clientId))
            return CLIENT_BY_IDS_CACHE.get(clientId);
        else
            return null;
    }

    public Client getClientByAlias(String alias) {
        if (StringUtils.isNotEmpty(alias))
            return CLIENT_BY_ALIAS_CACHE.get(alias);
        else
            return null;
    }

    public Client getClientByService(String service) {
        if (StringUtils.isNotEmpty(service))
            return CLIENT_BY_SERVICE_CACHE.get(service);
        else
            return null;
    }

}
