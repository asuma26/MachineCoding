package in.wynk.subscription.core.dao.entity;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.utils.BeanLocatorFactory;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Abhishek
 * @created 19/06/20
 */
@Data
@AnalysedEntity
@Builder
@Table(value = "subscription")
@Deprecated
public class Subscription implements Serializable {

    private static final long serialVersionUID = -7996842809903549020L;

    @Analysed
    @PrimaryKeyColumn(name = "service", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String service;
    @PrimaryKeyColumn(name = "uid", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String uid;
    @Analysed
    @PrimaryKeyColumn(name = "pack_group", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
    private String packGroup;
    @Analysed
    @Column(value = "partner_product_id")
    private int partnerProductId;
    @Analysed
    private boolean active;
    @Analysed
    @Column(value = "payment_method")
    private String paymentMethod;
    @Analysed
    @Column(value = "product_id")
    private int productId;
    @Column(value = "subscription_date")
    private Date subscriptionDate;
    @Column(value = "unsubscription_date")
    private Date unsubscriptionDate;
    @Column(value = "valid_till_date")
    private Date validTillDate;
    @Column(value = "next_charging_date")
    private Date nextChargingDate;
    @Column(value = "subscription_end_date")
    private Date subscriptionEndDate;

    public Map<String, String> getPaymentMetaData() {
        if (MapUtils.isEmpty(paymentMetaData)) {
            return new HashMap<>();
        }
        return paymentMetaData;
    }

    @Analysed
    @Column(value = "payment_meta_data")
    private Map<String, String> paymentMetaData;
    @Column(value = "deactivation_channel")
    private String deactivationChannel;
    @Column(value = "renewal_under_process")
    private Boolean renewalUnderProcess;
    @Analysed
    @Column(value = "auto_renewal_off")
    private Boolean autoRenewalOff;
    @Column(value = "subscription_in_progress")
    private Boolean subscriptionInProgress;
    @Analysed
    @Column(value = "subscription_status")
    private String subStatus;

    public boolean isFree() {
        return StringUtils.equalsIgnoreCase(getPaymentMethod(), BaseConstants.FREE);
    }

    public boolean isSubscriptionActive() {
        return this.getValidTillDate().getTime() > System.currentTimeMillis();
    }

    public boolean isSubscriptionInGracePeriod(TimeUnit timeUnit, int  gracePeriod) {
        return new Date(validTillDate.getTime() + timeUnit.toMillis(gracePeriod)).getTime() > System.currentTimeMillis();
    }

    public boolean isPaidActiveOrInGrace() {
        if(!isFree()) {
            String planId = getPaymentMetaData().get(BaseConstants.PLAN_ID);
            SubscriptionCachingService cachingService = BeanLocatorFactory.getBean(SubscriptionCachingService.class);
            if (StringUtils.isNotEmpty(planId) && cachingService.containsPlan(planId)) {
                Plan plan = cachingService.getPlan(planId);
                return getAutoRenewalOff() ? isSubscriptionActive() : isSubscriptionInGracePeriod(plan.getPeriod().getTimeUnit(), plan.getPeriod().getGrace());
            }
        }
        return false;
    }

    public long getValidTillWithGracePeriod(TimeUnit timeUnit, int  gracePeriod) {
        return validTillDate.getTime() + timeUnit.toMillis(gracePeriod);
    }
}
