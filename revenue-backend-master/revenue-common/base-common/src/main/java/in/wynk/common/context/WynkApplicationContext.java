package in.wynk.common.context;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class WynkApplicationContext {

    private final String clientId;
    private final String clientSecret;
    private final String clientAlias;
    private final Map<String, Object> meta;

    public <T> T getMeta(String key) {
        return (T) meta.get(key);
    }

}
