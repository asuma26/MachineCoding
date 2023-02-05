package in.wynk.session.repo.impl;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.session.constant.SessionConstant;
import in.wynk.session.constant.SessionErrorType;
import in.wynk.session.dto.Session;
import in.wynk.session.repo.ISessionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SessionRepositoryImpl implements ISessionRepository {

    private final ValueOperations<String, Session> valueOperations;

    public SessionRepositoryImpl(RedisTemplate<String, Session> redisTemplate) {
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public <T> void put(Session<T> session, long duration, TimeUnit timeUnit) {
        valueOperations.set(SessionConstant.SESSION_KEY +
                SessionConstant.COLON_DELIMITER +
                session.getId().toString(), session, duration, timeUnit);
    }

    public <T> Session<T> putIfAbsent(Session<T> session, long duration, TimeUnit timeUnit) {
        boolean isPersist = valueOperations.setIfAbsent(SessionConstant.SESSION_KEY +
                                                             SessionConstant.COLON_DELIMITER +
                                                             session.getId().toString(),
                                                             session, duration, timeUnit);
        if(!isPersist)
            throw new WynkRuntimeException(SessionErrorType.SESSION004);
        return session;
    }

    @Override
    public <T> Session<T> get(UUID id) {
        return valueOperations.get(SessionConstant.SESSION_KEY +
                SessionConstant.COLON_DELIMITER +
                id.toString());
    }

}
