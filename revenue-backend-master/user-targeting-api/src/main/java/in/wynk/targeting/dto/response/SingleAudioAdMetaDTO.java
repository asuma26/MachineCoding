package in.wynk.targeting.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SingleAudioAdMetaDTO extends AudioAdConfigResponse {

    @JsonProperty("ads")
    private final Map<String, AudioAdMetaDTO> audioSlots = new HashMap<>();
}
