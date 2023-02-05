package in.wynk.spel.configuration;

import in.wynk.spel.IExpressionParser;
import in.wynk.spel.IRuleEvaluator;
import in.wynk.spel.impl.DefaultExpressionParserImpl;
import in.wynk.spel.impl.DefaultRuleEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpelConfig {

    @Bean
    public IExpressionParser defaultExpressionParser() {
        return new DefaultExpressionParserImpl();
    }

    @Bean
    public IRuleEvaluator defaultRuleEvaluator(IExpressionParser expressionParser) {
        return new DefaultRuleEvaluator(expressionParser);
    }

}
