package in.wynk.session.repo;

import in.wynk.session.dto.Session;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface ISessionRepository {

    <T> void put(Session<T> session, long duration, TimeUnit timeUnit);

    <T> Session<T> putIfAbsent(Session<T> session, long duration, TimeUnit timeUnit);

    <T> Session<T> get(UUID id);

}
