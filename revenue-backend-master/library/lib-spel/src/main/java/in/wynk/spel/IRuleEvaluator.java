package in.wynk.spel;

import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;

public interface IRuleEvaluator {

    Object evaluate(String expression, Object rootObject);

    Object evaluate(String expression, FunctionalExpressionContextSupplier supplier);

    <T> T evaluate(String expression, Object rootObject, Class<T> target);

    <T> T evaluate(String expression, FunctionalExpressionContextSupplier supplier, Class<T> target);

    <T> T evaluate(Expression expression, FunctionalExpressionContextSupplier supplier, Class<T> target);

    <T> T evaluate(String expression, Object rootObject, ParserContext parserContext, Class<T> target);

    <T> T evaluate(String expression, FunctionalExpressionContextSupplier context, Object rootObject, Class<T> target);

    <T> T evaluate(String expression, FunctionalExpressionContextSupplier supplier, ParserContext parserContext, Class<T> target);

    <T> T evaluate(String expression, FunctionalExpressionContextSupplier context, Object rootObject, ParserContext parserContext, Class<T> target);

}
