package in.wynk.targeting.core.dao.entity.cassandra;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ad_targeting")
@Getter
@ToString
public class AdTargeting {

    @PrimaryKeyColumn(name = "uid", type = PrimaryKeyType.PARTITIONED)
    private String uid;
    @PrimaryKeyColumn(name = "adid", ordinal = 0)
    private String adid;
    private Boolean targeted;

    public String targetedString(){
        return targeted.toString();
    }

}
