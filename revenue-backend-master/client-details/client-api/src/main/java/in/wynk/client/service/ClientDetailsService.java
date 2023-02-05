package in.wynk.client.service;

import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.service.IClientDetailsService;

import java.util.Optional;

public class ClientDetailsService implements IClientDetailsService<Client> {

    private final ClientDetailsCachingService clientCachingService;

    public ClientDetailsService(ClientDetailsCachingService clientCachingService) {
        this.clientCachingService = clientCachingService;
    }

    @Override
    public Optional<Client> getClientDetails(String clientId) {
        return Optional.ofNullable(clientCachingService.getClientById(clientId));
    }

    @Override
    public Optional<Client> getClientDetailsByAlias(String clientAlias) {
        return Optional.ofNullable(clientCachingService.getClientByAlias(clientAlias));
    }

}
