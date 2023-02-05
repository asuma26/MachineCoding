package in.wynk.client.aspect.advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ClientAware {

    /*
     * Client Id to load client into client context
     */
    String clientId() default "";

    String clientAlias() default "";

}
