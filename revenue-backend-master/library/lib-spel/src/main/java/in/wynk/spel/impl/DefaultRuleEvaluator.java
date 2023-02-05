package in.wynk.spel.impl;

import in.wynk.spel.FunctionalExpressionContextSupplier;
import in.wynk.spel.IExpressionParser;
import in.wynk.spel.IRuleEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;

public class DefaultRuleEvaluator implements IRuleEvaluator {

    private final IExpressionParser expressionParser;

    public DefaultRuleEvaluator(IExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

    @Override
    public Object evaluate(String expressionStr, Object rootObject) {
        Expression expression = expressionParser.parse(expressionStr);
        return expression.getValue(rootObject);
    }

    @Override
    public Object evaluate(String expressionStr, FunctionalExpressionContextSupplier supplier) {
        Expression expression = expressionParser.parse(expressionStr);
        EvaluationContext context = supplier.get();
        return expression.getValue(context);
    }

    @Override
    public <T> T evaluate(String expressionStr, Object rootObject, Class<T> target) {
        Expression expression = expressionParser.parse(expressionStr);
        return expression.getValue(rootObject, target);
    }

    @Override
    public <T> T evaluate(String expressionStr, FunctionalExpressionContextSupplier supplier, Class<T> target) {
        Expression expression = expressionParser.parse(expressionStr);
        EvaluationContext context = supplier.get();
        return expression.getValue(context, target);
    }

    @Override
    public <T> T evaluate(Expression expression, FunctionalExpressionContextSupplier supplier, Class<T> target) {
        return expression.getValue(supplier.get(), target);
    }

    @Override
    public <T> T evaluate(String expressionStr, FunctionalExpressionContextSupplier supplier, Object rootObject, Class<T> target) {
        Expression expression = expressionParser.parse(expressionStr);
        EvaluationContext context = supplier.get();
        return expression.getValue(context, rootObject, target);
    }

    @Override
    public <T> T evaluate(String expressionStr, Object rootObject, ParserContext parserContext, Class<T> target) {
        if (parserContext == null)
            return evaluate(expressionStr, rootObject, target);
        Expression expression = expressionParser.parse(expressionStr, parserContext);
        return expression.getValue(rootObject, target);
    }

    @Override
    public <T> T evaluate(String expressionStr, FunctionalExpressionContextSupplier supplier, ParserContext parserContext, Class<T> target) {
        if (parserContext == null)
            return evaluate(expressionStr, supplier, target);
        EvaluationContext context = supplier.get();
        Expression expression = expressionParser.parse(expressionStr, parserContext);
        return expression.getValue(context, target);
    }

    @Override
    public <T> T evaluate(String expressionStr, FunctionalExpressionContextSupplier supplier, Object rootObject, ParserContext parserContext, Class<T> target) {
        if (parserContext == null)
            return evaluate(expressionStr, supplier, rootObject, target);
        EvaluationContext context = supplier.get();
        Expression expression = expressionParser.parse(expressionStr, parserContext);
        return expression.getValue(context, rootObject, target);
    }

}