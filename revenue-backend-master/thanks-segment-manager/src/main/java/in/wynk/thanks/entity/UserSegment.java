package in.wynk.thanks.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

/**
 * @author Abhishek
 * @created 2019-04-19
 */
@Deprecated
@Table("user_segment")
public class UserSegment {

    @PrimaryKeyColumn(name = "si", type = PrimaryKeyType.PARTITIONED)
    private String si;

    @PrimaryKeyColumn(name = "service", type = PrimaryKeyType.CLUSTERED)
    private String service;

    @PrimaryKeyColumn(name = "event", type = PrimaryKeyType.CLUSTERED)
    private String event;

    @Column(value = "servicePack")
    private String servicePack;

    //@Column(value = "lob")
    //private Lob lob;

    @Column(value = "thanks_timestamp")
    private Date thanksTimestamp;

    //@Column(value = "validity")
    private Date validity;

    public UserSegment() {
    }

    public UserSegment(String si, String service, String event, String servicePack,
                       Date thanksTimestamp, Date validity) {
        this.si = si;
        this.service = service;
        this.event = event;
        this.servicePack = servicePack;
        //this.lob = lob;
        this.thanksTimestamp = thanksTimestamp;
        this.validity = validity;
    }

    public UserSegment(ThanksUserSegment thanksUserSegment){
        this.si = thanksUserSegment.getEncryptedSi();
        this.event = thanksUserSegment.getEvent();
        this.servicePack = thanksUserSegment.getServicePack();
        this.service = thanksUserSegment.getService();
        this.validity = thanksUserSegment.getValidity();
        this.thanksTimestamp = thanksUserSegment.getThanksTimestamp();
    }

    public String getSi() {
        return si;
    }

    public void setSi(String si) {
        this.si = si;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getServicePack() {
        return servicePack;
    }

    public void setServicePack(String servicePack) {
        this.servicePack = servicePack;
    }

//  public Lob getLob() {
//    return lob;
//  }
//
//  public void setLob(Lob lob) {
//    this.lob = lob;
//  }

    public Date getThanksTimestamp() {
        return thanksTimestamp;
    }

    public void setThanksTimestamp(Date thanksTimestamp) {
        this.thanksTimestamp = thanksTimestamp;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    @Override
    public String toString() {
        return "UserSegment{"
                + "si='" + si + '\''
                + ", service='" + service + '\''
                + ", event='" + event + '\''
                + ", servicePack='" + servicePack + '\''
                // + ", lob=" + lob
                + ", thanksTimestamp=" + thanksTimestamp
                + ", validity=" + validity
                + '}';
    }
}

