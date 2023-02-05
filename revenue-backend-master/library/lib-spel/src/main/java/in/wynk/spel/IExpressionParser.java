package in.wynk.spel;

import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;

public interface IExpressionParser {

    Expression parse(String expression);

    Expression parse(String expression, ParserContext context);

}
