package in.wynk.targeting.services;

import in.wynk.advice.TimeIt;
import in.wynk.targeting.core.dao.entity.cassandra.AdTargeting;
import in.wynk.targeting.services.factory.UserTargetingSegments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AirtelTvUserTargetingSegments implements UserTargetingSegments {

    private final Logger logger = LoggerFactory.getLogger(AirtelTvUserTargetingSegments.class);

    @Autowired
    private CampaignTargetingFromCassandraTask campaignTargetingFromCassandraTask;

    @Override
    @TimeIt
    public Map<String, String> getUserTargetingSegments(String userId, String deviceId, Integer daysBefore) {
        logger.info("Fetching airteltv segments for user : " + userId);
        Map<String, String> response = new HashMap<>();
        try {
            List<AdTargeting> campaignTargeting = campaignTargetingFromCassandraTask.readCampaignTargeting(userId);
            if (campaignTargeting != null)
                campaignTargetingFromCassandraTask.addCampaignTargeting(response, campaignTargeting);
            logger.info("airteltv getUserTargetingSegments API response for user " + userId + " : " + response.toString());
        } catch (Throwable th) {
            logger.error("Error while fetching targeting segments for user " + userId + " : " + th.getMessage(), th);
        }
        return response;
    }

}
