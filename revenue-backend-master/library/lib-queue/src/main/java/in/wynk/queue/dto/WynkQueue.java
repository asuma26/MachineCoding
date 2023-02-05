package in.wynk.queue.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WynkQueue {
    int maxRetryCount() default 3;
    String queueName() default "";
    String delaySeconds() default "";
    QueueType queueType() default QueueType.STANDARD;
}