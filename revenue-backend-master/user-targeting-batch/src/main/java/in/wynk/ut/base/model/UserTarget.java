package in.wynk.ut.base.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("ad_targeting")
public class UserTarget {

    @PrimaryKeyColumn(name = "uid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String uid;
    @PrimaryKeyColumn(name = "adid", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String adid;
    @Column(value = "targeted")
    private Boolean targeted;

    public UserTarget(String uid, String adid, Boolean targeted) {
        this.uid = uid;
        this.adid = adid;
        this.targeted = targeted;
    }

    public String getUid() {
        return uid;
    }

    public String getAdid() {
        return adid;
    }

    public Boolean isTargeted() {
        return targeted;
    }

    public static class UserTargetBuilder {

        private String uid;
        private String adid;
        private Boolean targeted;

        public UserTarget  build() {
            return new UserTarget(uid, adid, targeted);
        }

        public UserTargetBuilder uid(String uid) {
            this.uid = uid;
            return this;
        }

        public UserTargetBuilder adid(String adid) {
            this.adid = adid;
            return this;
        }

        public UserTargetBuilder targeted(Boolean targeted) {
            this.targeted = targeted;
            return this;
        }

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
