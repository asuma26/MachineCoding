package in.wynk.order.service;

import in.wynk.exception.WynkRuntimeException;

import java.util.Optional;

public interface ITransactionalService<T> {

    Optional<T> save(T item);

    default Optional<T> update(T item) {
        throw new WynkRuntimeException("Method not implemented");
    }

    Optional<T> getByPrimaryId(String id);

    default Optional<T> getBySecondaryId(String id) {
        throw new WynkRuntimeException("Method not implemented");
    }

}
