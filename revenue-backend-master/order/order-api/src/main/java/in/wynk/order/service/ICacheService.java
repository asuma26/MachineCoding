package in.wynk.order.service;

import in.wynk.exception.WynkRuntimeException;

public interface ICacheService<T> {

    T save(T item);

    default T update(T item) { throw new WynkRuntimeException("Method not implemented"); }

    T getByPrimaryId(String id);

    default T getBySecondaryId(String id) { throw new WynkRuntimeException("Method not implemented"); };

}
