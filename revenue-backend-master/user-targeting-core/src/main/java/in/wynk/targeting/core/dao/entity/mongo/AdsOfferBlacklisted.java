package in.wynk.targeting.core.dao.entity.mongo;

import in.wynk.targeting.core.constant.AdType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
@Document(collection = "ads_blacklisted_offer")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdsOfferBlacklisted implements Serializable {

    private static final long serialVersionUID = -1104777757771082536L;

    @Field("ad_type")
    private AdType type;

    @Id
    private int id;

    @Field("ad_black_listed_cps")
    private List<String> blacklistedCps;
}
