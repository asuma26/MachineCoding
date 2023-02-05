package in.wynk.targeting.core.dao.entity.mongo.persona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentStreamDetails implements Serializable {
    private static final long serialVersionUID = 5880173148646576838L;
    @JsonProperty(value = "content_id")
    private String contentId;
    @JsonProperty(value = "content_partner")
    private String contentPartner;
    @JsonProperty(value = "stream_time")
    private double streamTime;
}
