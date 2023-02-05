package in.wynk.targeting.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class VideoAdConfigResponse implements Serializable {

    private static final long serialVersionUID = -9214200957452879242L;

    @JsonProperty("type")
    private final Map<String, String> meta = new HashMap<>();

}
