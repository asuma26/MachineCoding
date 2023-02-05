package in.wynk.targeting.services;

import in.wynk.targeting.core.dao.entity.cassandra.AdTargeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdTargetingService {

    private final Logger logger = LoggerFactory.getLogger(AdTargetingService.class);

    @Autowired
    private CampaignTargetingFromCassandraTask campaignTargetingFromCassandraTask;

    public Map<String, String> getUserSegments(String uid, String deviceId) {
        Map<String, String> apiResult = new HashMap<>();
        try {
            List<AdTargeting> campaignTargeting = campaignTargetingFromCassandraTask.readCampaignTargeting(uid);
            if (campaignTargeting != null)
                campaignTargetingFromCassandraTask.addCampaignTargeting(apiResult, campaignTargeting);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        apiResult.put("uid", uid);
        apiResult.put("deviceId", deviceId);
        return apiResult;
    }
}
