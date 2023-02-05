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
public class LanguageStreamDetails implements Serializable {
    private static final long serialVersionUID = 7139291472425935942L;
    private String language;
    @JsonProperty(value = "stream_time")
    private double streamTime;
}
