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
@Getter
@ToString
@Table(value = "offer_device_mapping")
public class OfferDeviceMapping implements Serializable {

    private static final long serialVersionUID = -6595374947615416868L;

    @PrimaryKeyColumn(name = "service", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String service;
    @PrimaryKeyColumn(name = "device_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String deviceId;
    @PrimaryKeyColumn(name = "offer_id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
    private int offerId;
    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID id;
    private String channel;
    private String tid;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferDeviceMapping that = (OfferDeviceMapping) o;
        return offerId == that.offerId &&
                Objects.equal(service, that.service) &&
                Objects.equal(deviceId, that.deviceId) &&
                Objects.equal(id, that.id) &&
                Objects.equal(channel, that.channel) &&
                Objects.equal(tid, that.tid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(service, deviceId, offerId, id, channel, tid);
    }

    public static final class Builder {
        private String service;
        private String deviceId;
        private int offerId;
        private UUID id;
        private int planId;
        private String channel;
        private String tid;

        private Builder() {
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder tid(String tid) {
            this.tid = tid;
            return this;
        }

        public Builder service(String service) {
            this.service = service;
            return this;
        }

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
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

        public OfferDeviceMapping build() {
            OfferDeviceMapping offerDeviceMapping = new OfferDeviceMapping();
            offerDeviceMapping.offerId = this.offerId;
            offerDeviceMapping.id = this.id;
            offerDeviceMapping.channel = StringUtils.isNotBlank(this.channel) ? this.channel : StringUtils.joinWith("|", "wcfapi", offerId, "app", planId);
            offerDeviceMapping.deviceId = this.deviceId;
            offerDeviceMapping.tid = this.tid;
            offerDeviceMapping.service = this.service;
            return offerDeviceMapping;
        }
    }
}
