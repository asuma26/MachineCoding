package in.wynk.order.core.dao.entity;

import in.wynk.common.enums.PaymentEvent;
import in.wynk.order.common.enums.OrderStatus;
import lombok.*;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders", indexes = {
        @Index(name = "partner_order_reference_index", columnList = "partner_order_reference")
})
public class Order implements Serializable, Persistable<String> {

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "order_id")
    private String id;
    @Column(name = "uid", updatable = false)
    private String uid;
    @Column(name = "plan_id", updatable = false)
    private int planId;
    @Column(name = "currency", updatable = false)
    private String currency;
    @Column(name = "amount", updatable = false)
    private double amount;
    @Column(name = "order_type", updatable = false)
    private String type;
    @Setter
    @Column(name = "order_state")
    private String status;
    @Column(name = "partner_order_reference", nullable = false, updatable = false)
    private String partnerOrderId;
    @Column(name = "payment_partner", updatable = false)
    private String partnerAlias;
    @Column(name = "payment_code", updatable = false)
    private String paymentCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", updatable = false)
    private Calendar createdOn;
    @Setter
    @Column(name = "updated_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updatedOn;

    private transient Map<String, Object> meta;

    @Builder.Default
    private transient boolean persisted = false;

    public <T> void putMeta(String key, T value) {
        if (MapUtils.isEmpty(meta)) {
            meta = new HashMap<>();
        }
        meta.put(key, value);
    }

    public <T> Optional<T> getMeta(String key) {
        if (MapUtils.isNotEmpty(meta) && meta.containsKey(key)) {
            return Optional.of((T) meta.get(key));
        }
        return Optional.empty();
    }

    public PaymentEvent getType() {
        return PaymentEvent.valueOf(type);
    }

    public OrderStatus getStatus() {
        return OrderStatus.valueOf(status);
    }

    public void persisted() {
        this.persisted = true;
    }

    @Override
    public boolean isNew() {
        return !persisted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return planId == order.planId &&
                Double.compare(order.amount, amount) == 0 &&
                id.equals(order.id) &&
                uid.equals(order.uid) &&
                currency.equals(order.currency) &&
                type.equals(order.type) &&
                status.equals(order.status) &&
                partnerAlias.equals(order.partnerAlias) &&
                paymentCode.equals(order.paymentCode) &&
                createdOn.equals(order.createdOn) &&
                Objects.equals(updatedOn, order.updatedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid, planId, currency, amount, type, status, partnerAlias, paymentCode, createdOn, updatedOn);
    }

}
