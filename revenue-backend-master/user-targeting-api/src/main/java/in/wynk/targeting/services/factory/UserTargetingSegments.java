package in.wynk.targeting.services.factory;

import java.util.Map;

public interface UserTargetingSegments {

    Map<String, String> getUserTargetingSegments(String userId, String deviceId, Integer daysBefore);

}
