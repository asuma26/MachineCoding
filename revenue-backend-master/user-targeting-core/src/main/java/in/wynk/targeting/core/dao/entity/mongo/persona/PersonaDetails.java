package in.wynk.targeting.core.dao.entity.mongo.persona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonaDetails implements Serializable {
    private static final long serialVersionUID = 7594104098971791345L;
    @JsonProperty(value = "total_stream_time")
    private double totalStreamTime;
    @JsonProperty(value = "genres_streams")
    private List<GenreStreamDetails> genreStreamDetails;
    @JsonProperty(value = "top_languages")
    private List<Map<String, String>> topLanguages;
    private Map<String, List<ContentStreamDetails>> streams;
    @JsonProperty(value = "lang_streams")
    private List<LanguageStreamDetails> languageStreamDetails;
    @JsonProperty(value = "top_genres")
    private List<Map<String, String>> topGenres;
    private DeviceDetails device;
    private UserDetails user;
}
