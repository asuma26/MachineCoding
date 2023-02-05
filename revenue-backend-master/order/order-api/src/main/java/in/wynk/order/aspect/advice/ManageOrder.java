package in.wynk.order.aspect.advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ManageOrder {

    /*
     * Order id to load order in order context
     */
    String orderId() default "";

    /*
     * Partner Order id to load order in order context
     */
    String partnerOrderId() default "";

}
