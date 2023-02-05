package in.wynk.session.config;

import in.wynk.session.constant.BeanConstant;
import in.wynk.session.dto.Session;
import in.wynk.session.repo.ISessionRepository;
import in.wynk.session.repo.impl.SessionRepositoryImpl;
import in.wynk.session.service.ISessionManager;
import in.wynk.session.service.impl.SessionManagerImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class SessionManagementConfigurer {

    private final RedisTemplate<String, Session> redisTemplate;

    public SessionManagementConfigurer(@Qualifier(BeanConstant.SESSION_REDIS_TEMPLATE) RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean(BeanConstant.SESSION_MANAGER_BEAN)
    public ISessionManager sessionManager() {
        return new SessionManagerImpl(sessionRepository());
    }

    private ISessionRepository sessionRepository() {
        return new SessionRepositoryImpl(redisTemplate);
    }

}
