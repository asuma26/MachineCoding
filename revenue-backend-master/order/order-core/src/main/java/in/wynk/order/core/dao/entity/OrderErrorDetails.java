package in.wynk.order.core.dao.entity;

import in.wynk.common.enums.PaymentEvent;
import in.wynk.order.common.enums.OrderStatusDetail;
import lombok.*;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Getter
@Builder
@Immutable
@Table(name = "order_error")
@AllArgsConstructor
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "OrderErrorDetails.findById", query = "SELECT o FROM OrderErrorDetails o WHERE o.id=:orderId", hints = @QueryHint(name = "javax.persistence.query.timeout", value = "200"))
})
public class OrderErrorDetails implements Serializable, Persistable<String> {

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "order_id")
    private String id;
    @Column(name = "uid")
    private String uid;
    @Column(name = "status_code")
    private String statusCode;
    @Column(name = "payment_partner")
    private String partnerAlias;
    @Column(name = "plan_id")
    private int planId;
    @Column(name = "order_type")
    private String type;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", updatable = false)
    private Calendar createdOn;

    @Override
    public boolean isNew() {
        return true;
    }

    public PaymentEvent getType() {
        return PaymentEvent.valueOf(type);
    }

    public OrderStatusDetail getStatusDetails() {
        return OrderStatusDetail.valueOf(statusCode);
    }

}
