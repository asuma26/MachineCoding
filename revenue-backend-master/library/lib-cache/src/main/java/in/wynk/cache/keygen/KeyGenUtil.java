package in.wynk.cache.keygen;

import in.wynk.cache.constant.BeanConstant;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class KeyGenUtil {

    @Autowired
    private ApplicationContext context;
    @Autowired
    @Qualifier(BeanConstant.CUSTOM_KEY_GENERATOR)
    private KeyGenerator keyGenerator;

    @Autowired
    @Qualifier(BeanConstant.SPEL_KEY_GENERATOR)
    private KeyGenerator spelKeyGenerator;

    public String getKey(String keyGenerator, String cacheKey, ProceedingJoinPoint joinPoint) {
        Class<?> target = joinPoint.getTarget().getClass();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] keyParams = methodSignature.getParameterNames();
        Object[] valueParams = joinPoint.getArgs();

        String key;

        if (StringUtils.isNotBlank(keyGenerator)) {
            key = (String) ((KeyGenerator) context.getBean(keyGenerator)).generate(target, method, valueParams);
        } else if (StringUtils.isNotBlank(cacheKey)) {
            key = ((SpelBasedKeyGenerator) spelKeyGenerator).generate(cacheKey, target, method, keyParams, valueParams);
        } else {
            key = (String) this.keyGenerator.generate(target, method, valueParams);
        }

        return key;
    }
}
