package in.wynk.spel;

import org.springframework.expression.EvaluationContext;

@FunctionalInterface
public interface FunctionalExpressionContextSupplier {

    EvaluationContext get();

}
