package in.wynk.subscription.core.dao.entity;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AnalysedEntity
@Table("thanks_user_segment")
public class ThanksUserSegment implements Serializable {

    private static final long serialVersionUID = 8696163066409734141L;
    @Analysed
    @PrimaryKeyColumn(name = "si", type = PrimaryKeyType.PARTITIONED)
    private String si;
    @Analysed
    @PrimaryKeyColumn(name = "service_pack")
    private String servicePack;
    @Analysed
    @PrimaryKeyColumn(name = "event")
    private String event;
    @Column
    @Analysed
    private String service;
    @Column("encrypted_si")
    @Analysed
    private String encryptedSi;
    @Column("thanks_timestamp")
    private Date thanksTimestamp;
    @Column
    @Analysed
    private String lob;
    @Column
    private Date validity;
    @Column("updated_at")
    private Date updatedAt;

    @Override
    public String toString() {
        return "ThanksUserSegment{" +
                "si='" + si + '\'' +
                ", servicePack='" + servicePack + '\'' +
                ", event=" + event +
                ", thanksTimestamp=" + thanksTimestamp +
                '}';
    }
}
