package in.wynk.targeting.core.dao.entity.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.wynk.targeting.core.constant.AdState;
import in.wynk.targeting.core.constant.AdSubType;
import in.wynk.targeting.core.constant.AdType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Builder
@ToString
@AllArgsConstructor
@Document(collection = "ads_config")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdConfig implements Serializable {

    private static final long serialVersionUID = -1104777757771082536L;
    @Id
    private String id;
    @Field("ad_id")
    @JsonProperty(value = "adId")
    private String slotId;
    @Field("ad_condition")
    @JsonIgnore
    private String condition;
    @Field("ad_type")
    @JsonProperty(value = "adType")
    private AdType type;
    @Field("client_id")
    @JsonProperty(value = "client_id")
    private String clientId;
    @Field("ad_sub_type")
    @JsonProperty(value = "adSubType")
    private AdSubType subType;
    @Setter
    @Field("ad_state")
    @JsonProperty(value = "adState")
    private AdState state;
    @Field("ad_meta")
    @JsonProperty(value = "adMeta")
    @Setter
    private AdMeta meta;

    public <V extends AdMeta> V getMeta() {
        return (V) meta;
    }

}
