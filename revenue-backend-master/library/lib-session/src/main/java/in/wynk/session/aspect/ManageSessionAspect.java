package in.wynk.session.aspect;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.session.aspect.advice.ManageSession;
import in.wynk.session.constant.BeanConstant;
import in.wynk.session.constant.SessionErrorType;
import in.wynk.session.context.SessionContextHolder;
import in.wynk.session.dto.Session;
import in.wynk.session.service.ISessionManager;
import in.wynk.spel.IRuleEvaluator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static in.wynk.session.constant.SessionConstant.SESSION_ID;

@Aspect
public class ManageSessionAspect {

    @Autowired
    @Qualifier(BeanConstant.SESSION_MANAGER_BEAN)
    private ISessionManager sessionManager;

    @Autowired
    private IRuleEvaluator ruleEvaluator;

    @Value("${session.duration:15}")
    private Integer duration;

    @Around(value = "execution(@in.wynk.session.aspect.advice.ManageSession * *.*(..))")
    public Object ManageSessionAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object returnObj;
        Object[] valueParams = joinPoint.getArgs();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ManageSession manageSession = method.getAnnotation(ManageSession.class);
        if (!StringUtils.isEmpty(manageSession.sessionId())) {
            UUID uuid;
            try {
                String id = parseSessionId(joinPoint, manageSession);
                AnalyticService.update(SESSION_ID, id);
                uuid = UUID.fromString(id);
            } catch (Exception e) {
                throw new WynkRuntimeException(SessionErrorType.SESSION002);
            }
            Session<?> session = sessionManager.get(uuid);
            if (session != null) {
                SessionContextHolder.set(session);
                returnObj = joinPoint.proceed(valueParams);
                sessionManager.put(SessionContextHolder.get(), duration, TimeUnit.MINUTES);
            } else {
                throw new WynkRuntimeException(SessionErrorType.SESSION001);
            }
        } else {
            throw new WynkRuntimeException(SessionErrorType.SESSION003);
        }
        return returnObj;
    }

    @AfterThrowing(value = "execution(@in.wynk.session.aspect.advice.ManageSession * *.*(..))", throwing = "throwable")
    public void ManageSessionAfterThrowing(Throwable throwable) {
        if (Objects.nonNull(SessionContextHolder.get())) {
            sessionManager.put(SessionContextHolder.get(), duration, TimeUnit.MINUTES);
        }
    }

    private String parseSessionId(JoinPoint joinPoint, ManageSession manageSession) {
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] keyParams = methodSignature.getParameterNames();
        Object[] valueParams = joinPoint.getArgs();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < Objects.requireNonNull(keyParams).length; i++) {
            context.setVariable(keyParams[i], valueParams[i]);
        }

        return ruleEvaluator.evaluate(manageSession.sessionId(), () -> context, String.class);
    }

}
