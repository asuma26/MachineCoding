package in.wynk.hystrix.service.impl;

import com.netflix.hystrix.*;
import in.wynk.hystrix.advice.WynkHystrixCommand;
import in.wynk.hystrix.dto.MethodInvocationData;
import in.wynk.hystrix.service.ICacheCommandManager;
import in.wynk.logging.BaseLoggingMarkers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CacheCommandHystrixManagerImpl implements ICacheCommandManager {

    private static final Map<MethodInvocationData, HystrixCommand.Setter> CACHED_COMMAND_MAP = new ConcurrentHashMap<>(100);

    private final ConfigurableBeanFactory configurableBeanFactory;

    public CacheCommandHystrixManagerImpl(ConfigurableBeanFactory configurableBeanFactory) {
        this.configurableBeanFactory = configurableBeanFactory;
    }

    @Override
    public HystrixCommand.Setter get(MethodInvocationData invocationData, WynkHystrixCommand commandAnnotation) {
        return CACHED_COMMAND_MAP.computeIfAbsent(invocationData, invocation -> {
            log.warn(BaseLoggingMarkers.WYNK_HYSTRIX, "Cache missed, computing Hystrix setter");
            return computeHystrixSetter(invocationData, commandAnnotation);
        });
    }

    private HystrixCommand.Setter computeHystrixSetter(
            MethodInvocationData invocationData, final WynkHystrixCommand commandAnnotation) {
        String commandGroupKey =
                StringUtils.isEmpty(commandAnnotation.commandGroupKey())
                        ? invocationData.getCallingClass().getCanonicalName()
                        : resolve(commandAnnotation.commandGroupKey(), String.class);
        String commandKey =
                StringUtils.isEmpty(commandAnnotation.commandKey())
                        ? invocationData.getSignature().toShortString()
                        : resolve(commandAnnotation.commandKey(), String.class);

        HystrixCommandProperties.Setter commandProperties =
                HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.valueOf(commandAnnotation.executionIsolationStrategy()))
                        .withExecutionTimeoutInMilliseconds(resolve(commandAnnotation.timeout(), Integer.class))
                        .withCircuitBreakerErrorThresholdPercentage(resolve(commandAnnotation.errorPercentage(), Integer.class))
                        .withMetricsRollingStatisticalWindowInMilliseconds(
                                resolve(commandAnnotation.metricsRollingwindow(), Integer.class))
                        .withCircuitBreakerRequestVolumeThreshold(resolve(commandAnnotation.requestVolumeThreshold(), Integer.class))
                        .withCircuitBreakerSleepWindowInMilliseconds(resolve(commandAnnotation.sleepWindow(), Integer.class))
                        .withMetricsHealthSnapshotIntervalInMilliseconds(
                                resolve(commandAnnotation.healthCheckInterval(), Integer.class));
        HystrixThreadPoolProperties.Setter threadPoolProperties =
                HystrixThreadPoolProperties.Setter()
                        .withCoreSize(resolve(commandAnnotation.threadPoolSize(), Integer.class))
                        .withMaxQueueSize(resolve(commandAnnotation.threadPoolQueueSize(), Integer.class));

        return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroupKey))
                .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
                .andCommandPropertiesDefaults(commandProperties)
                .andThreadPoolPropertiesDefaults(threadPoolProperties);
    }

    private <T> T resolve(String placeholder, Class<T> target) {
        String property = configurableBeanFactory.resolveEmbeddedValue(placeholder);
        return (T) ConvertUtils.convert(StringUtils.isEmpty(property) ? placeholder : property, target);
    }

}
