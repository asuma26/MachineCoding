package in.wynk.cache.keygen;

import in.wynk.spel.IRuleEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Objects;

public class SpelBasedKeyGenerator implements KeyGenerator {

    private final IRuleEvaluator ruleEvaluator;
    private final KeyGenerator defaultKeyGenerator;

    public SpelBasedKeyGenerator(IRuleEvaluator ruleEvaluator, KeyGenerator keyGenerator) {
        this.defaultKeyGenerator = keyGenerator;
        this.ruleEvaluator = ruleEvaluator;
    }

    public String generate( String expression,  Class<?> classObj,  Method method, String[] keyParams, Object  ... valueParams) {

        EvaluationRoot rootObject = new EvaluationRoot(method.getName(), classObj.getSimpleName());
        StandardEvaluationContext context = new StandardEvaluationContext(rootObject);

        for (int i = 0; i < Objects.requireNonNull(keyParams).length; i++) {
            context.setVariable(keyParams[i], valueParams[i]);
        }

        return ruleEvaluator.evaluate(expression, () -> context, String.class);
    }

    @Override
    public Object generate( Object classObj,  Method method, Object  ... valueParams) {
        return defaultKeyGenerator.generate(classObj, method, valueParams);
    }

    @Getter
    @AllArgsConstructor
    private static class EvaluationRoot {
        private final String methodName;
        private final String className;
    }

}
