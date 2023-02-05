package in.wynk.thanks.entity;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Deprecated
@AnalysedEntity
@Table("thanks_user_segment")
public class ThanksUserSegment {
    //    si | service_pack | event | encrpted_si | lob | service | thanks_timestamp | validity | updated_at
    @Analysed
    @PrimaryKeyColumn(name = "si", type = PrimaryKeyType.PARTITIONED)
    private String si;
    @Analysed
    @PrimaryKeyColumn(name = "service_pack")
    private String servicePack;
    @Analysed
    @PrimaryKeyColumn(name = "event")
    private String event;
    @Analysed
    @Column
    private String service;
    @Analysed
    @Column("encrypted_si")
    private String encryptedSi;
    @Column("thanks_timestamp")
    private Date thanksTimestamp;
    @Analysed
    @Column
    private String lob;
    @Column
    private Date validity;
    @Column("updated_at")
    private Date updatedAt;

    private ThanksUserSegment() {
    }

    public String getSi() {
        return si;
    }

    public void setSi(String si) {
        this.si = si;
    }

    public String getServicePack() {
        return servicePack;
    }

    public void setServicePack(String servicePack) {
        this.servicePack = servicePack;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEncryptedSi() {
        return encryptedSi;
    }

    public void setEncryptedSi(String encryptedSi) {
        this.encryptedSi = encryptedSi;
    }

    public Date getThanksTimestamp() {
        return thanksTimestamp;
    }

    public void setThanksTimestamp(Date thanksTimestamp) {
        this.thanksTimestamp = thanksTimestamp;
    }

    public String getLob() {
        return lob;
    }

    public void setLob(String lob) {
        this.lob = lob;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static final class Builder {
        private String si;
        private String servicePack;
        private String event;
        private String service;
        private String encryptedSi;
        private Date thanksTimestamp;
        private String lob;
        private Date validity;
        private Date updatedAt;

        public Builder(String encrypted_si, String servicePack, String event, Date thanksTimestamp) {
            this.encryptedSi = encrypted_si;
            this.servicePack = servicePack;
            this.event = event;
            this.thanksTimestamp = thanksTimestamp;
        }

        public Builder service(String service) {
            this.service = service;
            return this;
        }

        public Builder si(String si) {
            this.si = si;
            return this;
        }

        public Builder thanks_timestamp(Date thanksTimestamp) {
            this.thanksTimestamp = thanksTimestamp;
            return this;
        }

        public Builder lob(String lob) {
            this.lob = lob;
            return this;
        }

        public Builder validity(Date validity) {
            this.validity = validity;
            return this;
        }

        public Builder updated_at(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ThanksUserSegment build() {
            ThanksUserSegment thanksUserSegment = new ThanksUserSegment();
            thanksUserSegment.setSi(si);
            thanksUserSegment.setServicePack(servicePack);
            thanksUserSegment.setEvent(event);
            thanksUserSegment.setService(service);
            thanksUserSegment.setEncryptedSi(encryptedSi);
            thanksUserSegment.setThanksTimestamp(thanksTimestamp);
            thanksUserSegment.setLob(lob);
            thanksUserSegment.setValidity(validity);
            thanksUserSegment.setUpdatedAt(updatedAt);
            return thanksUserSegment;
        }
    }
}
