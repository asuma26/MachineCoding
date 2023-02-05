package in.wynk.targeting.services;

import in.wynk.advice.TimeIt;
import in.wynk.targeting.services.factory.UserTargetingSegments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class MusicUserTargetingSegments implements UserTargetingSegments {

    private final Logger logger = LoggerFactory.getLogger(MusicUserTargetingSegments.class);

    @Autowired
    private AdTargetingService adTargetingService;

    @Override
    @TimeIt
    public Map<String, String> getUserTargetingSegments(String userId, String deviceId, Integer daysBefore) {
        logger.info("Fetching music targeting segments for user : " + userId);
        try {
            Map<String, String> response = adTargetingService.getUserSegments(userId, deviceId);
            logger.info("music getUserTargetingSegments API response for user " + userId + " : " + response.toString());
            return response;
        } catch (Throwable th) {
            logger.error("Error while fetching targeting segments for user " + userId + " : " + th.getMessage(), th);
        }
        return Collections.emptyMap();
    }
}
