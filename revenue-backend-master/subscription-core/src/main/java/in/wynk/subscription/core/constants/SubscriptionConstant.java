package in.wynk.subscription.core.constants;

import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;

public interface SubscriptionConstant {
    String PROVISIONED_PLAN_DETAILS = "userPlanDetail";
    String PROVISIONED_PLAN = "plan";
    String PROVISIONED_OFFER = "offer";
    ParserContext SUBSCRIPTION_MESSAGE_TEMPLATE_CONTEXT = new TemplateParserContext("${", "}");

}
