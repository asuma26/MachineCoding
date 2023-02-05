package in.wynk.common.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("cors")
public class CorsProperties {

    private int maxAge;
    private Allowed allowed;

    @Getter
    @Setter
    public static class Allowed {

        private String[] origins;
        private String[] methods;

    }

}
