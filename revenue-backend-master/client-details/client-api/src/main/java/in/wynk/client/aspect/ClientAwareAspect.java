package in.wynk.client.aspect;

import in.wynk.auth.dao.entity.Client;
import in.wynk.auth.service.IClientDetailsService;
import in.wynk.client.aspect.advice.ClientAware;
import in.wynk.client.context.ClientContext;
import in.wynk.client.core.constant.ClientErrorType;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.spel.IRuleEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Aspect
public class ClientAwareAspect {

    @Autowired
    private IRuleEvaluator ruleEvaluator;
    @Autowired
    private IClientDetailsService<Client> clientDetailsService;

    @Before("execution(@in.wynk.client.aspect.advice.ClientAware * *.*(..))")
    public void beforeClientAware(JoinPoint pjp) {
        try {
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            ClientAware clientAware = method.getAnnotation(ClientAware.class);
            Optional<Client> clientOption = Optional.empty();
            if (StringUtils.isNotEmpty(clientAware.clientId())) {
                String clientId = parseId(clientAware.clientId(), pjp);
                clientOption = clientDetailsService.getClientDetails(clientId);
            }

            if (StringUtils.isNotEmpty(clientAware.clientAlias())) {
                String clientAlias = parseId(clientAware.clientAlias(), pjp);
                clientOption = clientDetailsService.getClientDetailsByAlias(clientAlias);
            }

            ClientContext.setClient(clientOption.orElseThrow(() -> new WynkRuntimeException(ClientErrorType.CLIENT001)));
        } catch (WynkRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new WynkRuntimeException(ClientErrorType.CLIENT002, e);
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
