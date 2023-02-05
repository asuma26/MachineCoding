package in.wynk.targeting.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.targeting.core.constant.AdConstants;
import in.wynk.targeting.core.utils.Utils;
import in.wynk.targeting.dto.UserTargetingSegmentsRequest;
import in.wynk.targeting.services.AdTargetingService;
import in.wynk.targeting.services.UserTargetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller("/wynk/userTargeting/.*")
public class UserTargetingController {

    private final Logger logger = LoggerFactory.getLogger(UserTargetingController.class);

    @Autowired
    private UserTargetingService userTargetingService;

    @Autowired
    private AdTargetingService adTargetingService;

    //TODO: Not in kibana
    @AnalyseTransaction(name = "userTargeting")
    @GetMapping(value = "/wynk/userTargeting/params")
    public Map<String, String> getUserTargetingSegments(@RequestHeader(AdConstants.MUSIC_HEADER_DID) String deviceIdHeader, @RequestParam List<String> client,
                                                        @RequestParam List<String> userId) {
        //uid from request context
        String uid = "";
        UserTargetingSegmentsRequest userTargetingSegmentsRequest = new UserTargetingSegmentsRequest();
        userTargetingSegmentsRequest.setDeviceId(Utils.getDeviceId(deviceIdHeader));
        userTargetingSegmentsRequest.setUid(uid);
        userTargetingSegmentsRequest.setUserIds(userId);
        userTargetingSegmentsRequest.setClients(client);
        Map<String, String> response = userTargetingService.getUserTargetingSegments(userTargetingSegmentsRequest, AdConstants.SONG_PLAY_COUNT_DAYS, null);
        AnalyticService.update("response", response.toString());
        return response;
    }
}
