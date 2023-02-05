package in.wynk.session.service.impl;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.session.dto.Session;
import in.wynk.session.repo.ISessionRepository;
import in.wynk.session.service.ISessionManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static in.wynk.session.constant.SessionConstant.SESSION_ID;

public class SessionManagerImpl implements ISessionManager {

    private final ISessionRepository sessionRepository;

    public SessionManagerImpl(ISessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    private static UUID generateUUID() {
        return UUID.randomUUID();
    }

    @Override
    public <T> Session<T> init(T body, long duration, TimeUnit timeUnit) {
        AnalyticService.update(body);
        Session<T> session =  sessionRepository.putIfAbsent(Session.<T>builder()
                .id(generateUUID())
                .body(body)
                .build(),duration, timeUnit);
        AnalyticService.update(SESSION_ID, session.getId().toString());
        return session;
    }

    @Override
    public <T> void put(Session<T> session, long duration, TimeUnit timeUnit) {
         sessionRepository.put(session, duration, timeUnit);
    }

    @Override
    public <T> Session<T> get(UUID id) {
        return sessionRepository.get(id);
    }
}
