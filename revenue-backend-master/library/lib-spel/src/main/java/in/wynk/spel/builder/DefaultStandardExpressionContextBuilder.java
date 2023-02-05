package in.wynk.spel.builder;

import org.springframework.expression.*;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

public class DefaultStandardExpressionContextBuilder {

    StandardEvaluationContext context = new StandardEvaluationContext();

    private DefaultStandardExpressionContextBuilder() {}

    public StandardEvaluationContext build() {
        return context;
    }

    public static DefaultStandardExpressionContextBuilder builder() {
        return new DefaultStandardExpressionContextBuilder();
    }


    public DefaultStandardExpressionContextBuilder rootObject( Object rootObject) {
        context.setRootObject(rootObject);
        return this;
    }

    public DefaultStandardExpressionContextBuilder propertyAccessors( List<PropertyAccessor> propertyAccessors) {
        context.setPropertyAccessors(propertyAccessors);
        return this;
    }

    public DefaultStandardExpressionContextBuilder constructorResolvers( List<ConstructorResolver> constructorResolvers) {
        context.setConstructorResolvers(constructorResolvers);
        return this;
    }

    public DefaultStandardExpressionContextBuilder beanResolver( BeanResolver beanResolver) {
        context.setBeanResolver(beanResolver);
        return this;
    }

    public DefaultStandardExpressionContextBuilder typeLocator( TypeLocator typeLocator) {
        context.setTypeLocator(typeLocator);
        return this;
    }

    public DefaultStandardExpressionContextBuilder typeConverter( TypeConverter typeConverter) {
        context.setTypeConverter(typeConverter);
        return this;
    }

    public DefaultStandardExpressionContextBuilder typeComparator( TypeComparator typeComparator) {
        context.setTypeComparator(typeComparator);
        return this;
    }

    public DefaultStandardExpressionContextBuilder operatorOverloader( OperatorOverloader operatorOverloader) {
        context.setOperatorOverloader(operatorOverloader);
        return this;
    }

    public DefaultStandardExpressionContextBuilder variable( String key,  Object value) {
        context.setVariable(key, value);
        return this;
    }

}
