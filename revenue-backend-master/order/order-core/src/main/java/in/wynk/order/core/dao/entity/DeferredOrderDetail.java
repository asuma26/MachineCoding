package in.wynk.order.core.dao.entity;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deferred_order_details")
@NamedQueries({
        @NamedQuery(name = "DeferredOrderDetail.findById", query = "SELECT o FROM DeferredOrderDetail o WHERE o.id=:orderId", hints = @QueryHint(name = "javax.persistence.query.timeout", value = "300"))
})
public class DeferredOrderDetail implements Serializable, Persistable<String> {

    @Id
    @Column(name = "order_id")
    private String id;
    @Column(name = "is_pre_fulfilled")
    private boolean preFulfilled;
    @Column(name = "callback_url")
    private String callbackUrl;
    @Setter
    @Column(name = "order_status")
    private String status;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deferred_until_date", updatable = false)
    private Calendar untilDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_until_date", updatable = false)
    private Calendar validUntil;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", updatable = false)
    private Calendar createdOn;
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_timestamp")
    private Calendar updatedOn;
    @Builder.Default
    private transient boolean persisted = false;

    public void persisted() {
        persisted = true;
    }

    @Override
    public boolean isNew() {
        return !persisted;
    }
}

