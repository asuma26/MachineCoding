package in.wynk.queue.config.properties;

import com.amazonaws.regions.Regions;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("amazon")
public class AmazonSdkProperties {

    private SDK sdk;

    @Getter
    @Setter
    public static class SDK {

        private String regions;

        public Regions getRegions() {
            return Regions.fromName(regions);
        }

    }

}
