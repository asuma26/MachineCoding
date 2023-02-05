package in.wynk.auth.service;

import in.wynk.auth.dao.entity.Client;

import java.util.Optional;

/**
 * @author Abhishek
 * @created 21/09/20
 */

public interface IClientDetailsService<T extends Client> {

    Optional<T> getClientDetails(String clientId);

    Optional<T> getClientDetailsByAlias(String clientAlias);
}
