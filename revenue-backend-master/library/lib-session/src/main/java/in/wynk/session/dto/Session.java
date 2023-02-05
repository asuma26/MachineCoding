package in.wynk.session.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
public class Session<T> {

    private final UUID id;
    private final T body;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Session(@JsonProperty("id") UUID id,@JsonProperty ("body") T body) {
        this.id = id;
        this.body = body;
    }

}
