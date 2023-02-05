package in.wynk.aspect;

import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.logging.WynkErrorEncoder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclarePrecedence;
import org.slf4j.MDC;

import static in.wynk.exception.constants.ExceptionConstants.ERROR_CODE;
import static in.wynk.exception.constants.ExceptionConstants.REQUEST_ID;
import static in.wynk.logging.constants.LoggingConstants.SOURCE_HOST;
import static in.wynk.logging.constants.LoggingConstants.SOURCE_IP;

@Slf4j
@Aspect
@DeclarePrecedence("com.github.annotation.analytic.core.service.Analytic")
public class AnalyticAspect {

    @Before("execution(@com.github.annotation.analytic.core.annotations.AnalyseTransaction * *.*(..))")
    public void beforeEntering(JoinPoint joinPoint) {
        initAnalyticContext(joinPoint);
    }

    private void initAnalyticContext(JoinPoint joinPoint) {
        AnalyticService.update(REQUEST_ID, MDC.get(REQUEST_ID));
        AnalyticService.update(SOURCE_IP, WynkErrorEncoder.getIp());
        AnalyticService.update(SOURCE_HOST, WynkErrorEncoder.getHost());
    }

    @AfterThrowing(value = "execution(@com.github.annotation.analytic.core.annotations.AnalyseTransaction * *.*(..))", throwing = "exception")
    public void afterThrowing(WynkRuntimeException exception) {
        MDC.put(ERROR_CODE, exception.getErrorCode());
        AnalyticService.update(ERROR_CODE, exception.getErrorCode());
    }
}
