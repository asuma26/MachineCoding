package in.wynk.hystrix.advice;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WynkHystrixCommand {

    String enabled() default "false";

    /**
     * All defaults set to hystrix core defaults
     * For more detailed understanding of configuration visit https://github.com/Netflix/Hystrix/wiki/Configuration
     */

    String commandKey() default "";

    String commandGroupKey() default "";

    /**
     * Sets the name of fallback method to be executed in case of error / circuit open.
     */
    String fallbackMethod();

    /**
     * Sets the time in milliseconds after which the caller will walk away from the command execution.
     *
     * @return
     */
    String timeout();

    /**
     * Sets the error percentage at or above which the circuit should trip open.
     *
     * @return
     */
    String errorPercentage() default "50";

    /**
     * Sets the threadPool size.
     * @return
     */
    String threadPoolSize() default "10";

    /**
     * Sets the max queue size fot the threadPool.
     *
     * @return
     */
    String threadPoolQueueSize() default "-1";

    /**
     * Sets the duration of the statistical rolling window, in milliseconds.
     *
     * @return
     */
     String metricsRollingwindow() default "10000";

    /**
     * Sets the minimum number of requests in a rolling window that will trip the circuit
     *
     * @return
     */
    String requestVolumeThreshold() default "20";

    /**
     * Sets the amount of time, after tripping the circuit, to reject requests before allowing attempts again to determine if the circuit should again be closed
     *
     * @return
     */
    String sleepWindow() default "5000";

    /**
     * Sets the time to wait, in milliseconds, between allowing health snapshots to be taken
     *
     * @return
     */
    String healthCheckInterval() default "500";

    String executionIsolationStrategy() default "THREAD";

    String defaultFallback() default "";

}
