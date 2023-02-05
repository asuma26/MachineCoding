package in.wynk.thanks.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Deprecated
@Table("thanks_segment_new")
public class ThanksOldSegment {

    @PrimaryKeyColumn(name = "si", type = PrimaryKeyType.PARTITIONED)
    private String si;

    @PrimaryKeyColumn(name = "servicePack", type = PrimaryKeyType.CLUSTERED)
    private String servicePack;

    @PrimaryKeyColumn(name = "event", type = PrimaryKeyType.CLUSTERED)
    private String event;

    @Column(value = "service")
    private String service;

    @Column(value = "lob")
    private String lob;

    @Column(value = "validity")
    private Date validity;

    @Column(value = "thanks_timestamp")
    private Date thanksTimestamp;

    public ThanksOldSegment(String si, String servicePack, String event, String service, String lob, Date validity, Date thanksTimestamp) {
        this.si = si;
        this.servicePack = servicePack;
        this.event = event;
        this.service = service;
        this.lob = lob;
        this.validity = validity;
        this.thanksTimestamp = thanksTimestamp;
    }

    public ThanksOldSegment(ThanksUserSegment thanksUserSegment){
        this.si = thanksUserSegment.getEncryptedSi();
        this.event = thanksUserSegment.getEvent();
        this.servicePack = thanksUserSegment.getServicePack();
        this.service = thanksUserSegment.getService();
        this.lob = thanksUserSegment.getLob();
        this.validity = thanksUserSegment.getValidity();
        this.thanksTimestamp = thanksUserSegment.getThanksTimestamp();
    }

    public ThanksOldSegment() {
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

    public Date getThanksTimestamp() {
        return thanksTimestamp;
    }

    public void setThanksTimestamp(Date thanksTimestamp) {
        this.thanksTimestamp = thanksTimestamp;
    }

    @Override
    public String toString() {
        return "ThanksSegment{"
                + "si='" + si + '\''
                + ", servicePack='" + servicePack + '\''
                + ", event='" + event + '\''
                + ", service='" + service + '\''
                + ", lob='" + lob + '\''
                + ", validity=" + validity
                + ", thanksTimestamp=" + thanksTimestamp
                + '}';
    }
}
