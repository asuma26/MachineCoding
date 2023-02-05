package in.wynk.subscription.core.dao.entity;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Abhishek
 * @created 20/06/20
 */
@Deprecated
@Getter
@ToString
@Table(value = "offer_msisdn_mapping")
public class OfferMsisdnMapping implements Serializable {

    private static final long serialVersionUID = 2877302341782829042L;

    @PrimaryKeyColumn(name = "service", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String service;
    @PrimaryKeyColumn(name = "msisdn", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String msisdn;
    @PrimaryKeyColumn(name = "offer_id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
    private int offerId;
    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID id;
    private String channel;
    private String tid;

    public static Builder builder() {
        return new Builder();
    }

    public long getTimestamp() {
        return this.id.timestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferMsisdnMapping that = (OfferMsisdnMapping) o;
        return offerId == that.offerId &&
                Objects.equal(service, that.service) &&
                Objects.equal(msisdn, that.msisdn) &&
                Objects.equal(id, that.id) &&
                Objects.equal(channel, that.channel) &&
                Objects.equal(tid, that.tid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(service, msisdn, offerId, id, channel, tid);
    }

    public static final class Builder {
        private String service;
        private String msisdn;
        private int offerId;
        private UUID id;
        private String channel;
        private String tid;
        private int planId;

        private Builder() {
        }

        public Builder service(String service) {
            this.service = service;
            return this;
        }

        public Builder msisdn(String msisdn) {
            this.msisdn = msisdn;
            return this;
        }

        public Builder offerId(int offerId) {
            this.offerId = offerId;
            return this;
        }

        public Builder planId(int planId) {
            this.planId = planId;
            return this;
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder tid(String tid) {
            this.tid = tid;
            return this;
        }

        public OfferMsisdnMapping build() {
            OfferMsisdnMapping offerMsisdnMapping = new OfferMsisdnMapping();
            offerMsisdnMapping.msisdn = this.msisdn;
            offerMsisdnMapping.offerId = this.offerId;
            offerMsisdnMapping.channel = StringUtils.isNotBlank(this.channel) ? this.channel : StringUtils.joinWith("|", "wcfapi", offerId, "app", planId);
            offerMsisdnMapping.id = this.id;
            offerMsisdnMapping.service = this.service;
            offerMsisdnMapping.tid = this.tid;
            return offerMsisdnMapping;
        }
    }
}