package in.wynk.session.service;

import in.wynk.session.dto.Session;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface ISessionManager {

    <T> Session<T> init(T body, long duration, TimeUnit timeUnit);

    <T> void put(Session<T> session, long duration, TimeUnit timeUnit);

    <T> Session<T> get(UUID id);

}
