package in.wynk.subscription.core.dao.entity;


import lombok.Getter;

@Getter
public class Discount {

    private int percent;
    private int hierarchy;
    private String ruleExpression;

}
