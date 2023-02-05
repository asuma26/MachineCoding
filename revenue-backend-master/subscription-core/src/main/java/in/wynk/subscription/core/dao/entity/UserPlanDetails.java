package in.wynk.subscription.core.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.wynk.common.utils.BeanLocatorFactory;
import in.wynk.subscription.common.enums.PlanType;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
@ToString
@Table(value = "user_plan_details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPlanDetails implements Serializable {

    private static final long serialVersionUID = 5684899191542827180L;

    @PrimaryKeyColumn(name = "service", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private final String service;
    @PrimaryKeyColumn(name = "uid", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private final String uid;
    @PrimaryKeyColumn(name = "plan_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private final int planId;
    @Column(value = "offer_id")
    private final int offerId;
    @Column(value = "payment_code")
    private final String paymentCode;
    @Column(value = "payment_channel")
    private final String paymentChannel;
    @Column(value = "plan_type")
    private final String planType;
    @Column(value = "start_date")
    private final Date startDate;
    @Setter
    @Column(value = "end_date")
    private Date endDate;
    @Setter
    @Column(value = "unsubscribe_date")
    private Date unsubscribeOn;
    @Setter
    @Column(value = "auto_renew")
    private boolean autoRenew;
    @Column(value = "meta")
    private Map<String, String> meta;
    @Setter
    @Column(value = "reference_id")
    private String referenceId;
    @Column(value = "plan_count")
    private final int planCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPlanDetails)) return false;
        UserPlanDetails that = (UserPlanDetails) o;
        return planId == that.planId &&
                service.equals(that.service) &&
                uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, uid, planId);
    }

    @JsonIgnore
    public boolean isActive() {
        return endDate.getTime() > System.currentTimeMillis();
    }

    @JsonIgnore
    public boolean isFree() {
        return PlanType.fromString(planType) == PlanType.FREE;
    }

    @JsonIgnore
    public boolean isPaid() {
        return PlanType.fromString(planType) != PlanType.FREE;
    }

    public boolean isSubscriptionInGracePeriod(TimeUnit timeUnit, int  gracePeriod) {
        return endDate.getTime() < System.currentTimeMillis() && new Date(endDate.getTime() + timeUnit.toMillis(gracePeriod)).getTime() > System.currentTimeMillis();
    }

    public boolean isPaidActiveOrInGrace(){
        SubscriptionCachingService cachingService = BeanLocatorFactory.getBean(SubscriptionCachingService.class);
        if(isPaid()){
            if(isActive()){
                return true;
            } else if(isAutoRenew() && cachingService.containsPlan(getPlanId())){
                Plan plan = cachingService.getPlan(getPlanId());
                return isSubscriptionInGracePeriod(plan.getPeriod().getTimeUnit(), plan.getPeriod().getGrace());
            }
        }
        return false;
    }

    public boolean isFreeActiveOrPaidActive(){
        return isActive() || isPaidActiveOrInGrace();
    }

    public long getValidTillWithGracePeriod(TimeUnit timeUnit, int  gracePeriod) {
        return endDate.getTime() + timeUnit.toMillis(gracePeriod);
    }

    public Map<String, String> getMeta() {
        if (Objects.isNull(meta)) {
            meta = new HashMap<>();
        }
        return meta;
    }

}
