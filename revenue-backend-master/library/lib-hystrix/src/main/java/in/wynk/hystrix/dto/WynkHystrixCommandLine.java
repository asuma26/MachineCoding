package in.wynk.hystrix.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import com.netflix.hystrix.HystrixCommand;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.hystrix.factory.MethodFinderFactory;
import in.wynk.logging.BaseLoggingMarkers;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WynkHystrixCommandLine extends HystrixCommand<WynkHystrixCommandLine.WynkHystrixResponse> {

    private final ProceedingJoinPoint pjp;
    private final String fallbackMethodName;
    private final MethodInvocationData invocationData;
    private final WynkHystrixResponse response;

    public WynkHystrixCommandLine(String fallbackMethodName, MethodInvocationData invocationData, ProceedingJoinPoint pjp, Setter setter) {
        super(setter);
        this.pjp = pjp;
        response = new WynkHystrixResponse();
        this.invocationData = invocationData;
        this.fallbackMethodName = fallbackMethodName;
    }

    @Override
    protected WynkHystrixResponse run() {
        long startTime = System.currentTimeMillis();
        String methodName = pjp.getSignature().getName();
        try {
            Object response = pjp.proceed(pjp.getArgs());
            this.response.setAnalytics(methodName + "-executionTime", System.currentTimeMillis() - startTime);
            this.response.setResponse(response);
        } catch (Throwable throwable) {
            throw new WynkRuntimeException(throwable);
        } finally {
            this.response.setAnalytics(methodName + "-TotalTime", System.currentTimeMillis() - startTime);
        }
        return this.response;
    }

    @Override
    protected WynkHystrixResponse getFallback() {
        long startTime = System.currentTimeMillis();
        try {
            log.error(
                    BaseLoggingMarkers.WYNK_HYSTRIX_ERROR,
                    "key={}, circuitBroken={}, time={}, args={}",
                    getCommandKey(),
                    isCircuitBreakerOpen(),
                    getExecutionTimeInMilliseconds(),
                    invocationData.getArgs(),
                    getExecutionException());
            this.response.setAnalytics("isFallback", Boolean.TRUE);
            Object output = MethodFinderFactory.getMethodFinder().find(fallbackMethodName, invocationData, getExecutionException()).invoke();
            this.response.setResponse(output);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new WynkRuntimeException(e);
        } finally {
            this.response.setAnalytics("fallbackMethod-" + fallbackMethodName + "-executionTime", System.currentTimeMillis() - startTime);
        }
        return this.response;
    }

    @Override
    public boolean isCircuitBreakerOpen() {
        this.response.setAnalytics("circuitBreakerOpen", super.isCircuitBreakerOpen());
        return super.isCircuitBreakerOpen();
    }

    @AnalysedEntity
    public static class WynkHystrixResponse {
        @Analysed
        private final Map<String, Object> hystrix = new HashMap<>();
        @lombok.Getter
        @lombok.Setter
        private Object response;

        public Map<String, Object> getHystrix() {
            return hystrix;
        }

        public void setAnalytics(String key, Object value) {
            this.hystrix.put(key, value);
        }
    }
}
