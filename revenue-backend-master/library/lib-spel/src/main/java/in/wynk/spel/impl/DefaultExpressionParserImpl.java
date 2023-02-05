package in.wynk.spel.impl;

import in.wynk.spel.IExpressionParser;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class DefaultExpressionParserImpl implements IExpressionParser {

    private SpelExpressionParser parser = new SpelExpressionParser();

    @Override
    public Expression parse(String expression) {
        return parser.parseExpression(expression);
    }

    @Override
    public Expression parse(String expression, ParserContext context) {
        return parser.parseExpression(expression, context);
    }

}
