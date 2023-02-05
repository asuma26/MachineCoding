package in.wynk.order.aspect;

import in.wynk.exception.WynkRuntimeException;
import in.wynk.order.aspect.advice.ManageOrder;
import in.wynk.order.context.OrderContext;
import in.wynk.order.core.constant.OrderErrorType;
import in.wynk.order.core.dao.entity.Order;
import in.wynk.order.service.ICacheService;
import in.wynk.spel.IRuleEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@Aspect
public class ManageOrderAspect {

    @Autowired
    private IRuleEvaluator ruleEvaluator;
    @Autowired
    private ICacheService<Order> orderCacheService;

    @Before("execution(@in.wynk.order.aspect.advice.ManageOrder * *.*(..))")
    public void beforeOrderAware(JoinPoint pjp) {
        try {
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            ManageOrder manageOrder = method.getAnnotation(ManageOrder.class);
            Order order = null;
            if (StringUtils.isNotEmpty(manageOrder.partnerOrderId())) {
                String partnerOrderId = parseId(manageOrder.partnerOrderId(), pjp);
                order = orderCacheService.getBySecondaryId(partnerOrderId);
            } else if (StringUtils.isNotEmpty(manageOrder.orderId())) {
                String wynkOrderId = parseId(manageOrder.orderId(), pjp);
                order = orderCacheService.getByPrimaryId(wynkOrderId);
            }
            if (order != null) order.persisted();
            OrderContext.setOrder(order);
        } catch (WynkRuntimeException ignore) {
            OrderContext.clear();
        } catch (Exception e) {
            throw new WynkRuntimeException(OrderErrorType.ORD010, e);
        }
    }

    private String parseId(String orderIdReference, JoinPoint joinPoint) {
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] keyParams = methodSignature.getParameterNames();
        Object[] valueParams = joinPoint.getArgs();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < Objects.requireNonNull(keyParams).length; i++) {
            context.setVariable(keyParams[i], valueParams[i]);
        }

        return ruleEvaluator.evaluate(orderIdReference, () -> context, String.class);
    }

}
