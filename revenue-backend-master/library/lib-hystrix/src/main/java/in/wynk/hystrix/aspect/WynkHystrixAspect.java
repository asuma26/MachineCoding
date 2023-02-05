package in.wynk.hystrix.aspect;

import com.github.annotation.analytic.core.service.AnalyticService;
import com.netflix.hystrix.HystrixCommand;
import in.wynk.hystrix.advice.WynkHystrixCommand;
import in.wynk.hystrix.config.properties.WynkHystrixProperty;
import in.wynk.hystrix.dto.MethodInvocationData;
import in.wynk.hystrix.dto.WynkHystrixCommandLine;
import in.wynk.hystrix.service.ICacheCommandManager;
import in.wynk.spel.IRuleEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@Aspect
public class WynkHystrixAspect {

    @Autowired
    private WynkHystrixProperty globalHystrixProperty;
    @Autowired
    private ICacheCommandManager cacheCommandManager;
    @Autowired
    private IRuleEvaluator ruleEvaluator;

    @Around(value = "execution(@in.wynk.hystrix.advice.WynkHystrixCommand * *.*(..))")
    public Object handleCircuit(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        WynkHystrixCommand wynkHystrixCommand = method.getAnnotation(WynkHystrixCommand.class);
        boolean isEnabled = parseEnable(wynkHystrixCommand.enabled(), pjp);
        if (globalHystrixProperty.isEnabled() && isEnabled) {
            MethodInvocationData invocationData = MethodInvocationData.from(pjp);
            HystrixCommand.Setter setter = cacheCommandManager.get(invocationData, wynkHystrixCommand);
            WynkHystrixCommandLine.WynkHystrixResponse hystrixResponse = new WynkHystrixCommandLine(wynkHystrixCommand.fallbackMethod(), invocationData, pjp, setter).execute();
            AnalyticService.update(hystrixResponse);
            return hystrixResponse.getResponse();
        } else {
            return pjp.proceed(pjp.getArgs());
        }
    }

    private boolean parseEnable(String enableKeyRef, JoinPoint joinPoint) {
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] keyParams = methodSignature.getParameterNames();
        Object[] valueParams = joinPoint.getArgs();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < Objects.requireNonNull(keyParams).length; i++) {
            context.setVariable(keyParams[i], valueParams[i]);
        }

        return ruleEvaluator.evaluate(enableKeyRef, () -> context, Boolean.class);
    }

}
