package in.wynk.targeting.services.factory;

import in.wynk.targeting.services.AirtelTvUserTargetingSegments;
import in.wynk.targeting.services.MusicUserTargetingSegments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTargetingSegmentsFactory {

    @Autowired
    private AirtelTvUserTargetingSegments airtelTvSegments;

    @Autowired
    private MusicUserTargetingSegments musicSegments;

    public UserTargetingSegments getUserTargetingSegmentsClient(String client) {
        if("airteltv".equalsIgnoreCase(client)) {
            return airtelTvSegments;
        }
        else if("music".equalsIgnoreCase(client)) {
            return musicSegments;
        }
        return null;
    }
}
