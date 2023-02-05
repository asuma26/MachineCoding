package in.wynk.auth.dao.entity;

import in.wynk.exception.WynkRuntimeException;

import java.util.List;
import java.util.Optional;

/**
 * @author Abhishek
 * @created 28/08/20
 */

public interface Client {

    String getClientId();

    String getAlias();

    String getClientSecret();

    List<String> getAuthorities();

    default <T> Optional<T> getMeta(String key) {
        throw new WynkRuntimeException("Method is not implemented");
    }
}
