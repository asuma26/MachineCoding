package in.wynk.targeting.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.targeting.constant.ContollerType;
import in.wynk.targeting.core.constant.AdConstants;
import in.wynk.targeting.core.utils.Utils;
import in.wynk.targeting.dto.UserTargetingSegmentsRequest;
import in.wynk.targeting.services.AdTargetingService;
import in.wynk.targeting.services.UserTargetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static in.wynk.common.constant.BaseConstants.UID;

@Controller("TargetingS2SController")
public class TargetingS2SController {

    private final Logger logger = LoggerFactory.getLogger(TargetingS2SController.class);

    @Autowired
    private UserTargetingService userTargetingService;

    @Autowired
    private AdTargetingService adTargetingService;

    @GetMapping(value = "/wynk/v1/s2s/userTargeting")
    @AnalyseTransaction(name = "s2sOldUserTargeting")
    public ResponseEntity<Map<String, String>> getUserTargetingSegmentsForInternal(@RequestHeader(value = AdConstants.MUSIC_HEADER_DID, required = false) String deviceIdHeader,
                                                                   @RequestParam("uid") List<String> allUids) {
        String uid = allUids.get(0);
        String deviceId = Utils.getDeviceId(deviceIdHeader);
        AnalyticService.update(UID, uid);
        Map<String, String> response = adTargetingService.getUserSegments(uid, deviceId);
        AnalyticService.update("response", response.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/wynk/v2/s2s/userTargeting/params")
    @AnalyseTransaction(name = "s2sUserTargeting")
    public ResponseEntity<Map<String, String>> getUserTargetingSegments(@RequestHeader(value = AdConstants.MUSIC_HEADER_DID, required = false) String deviceIdHeader,
                                                                       @RequestParam(value = "userId", required = false) List<String> allUids,
                                                                       @RequestParam("client") List<String> clients) {
        UserTargetingSegmentsRequest userTargetingSegmentsRequest = new UserTargetingSegmentsRequest();
        userTargetingSegmentsRequest.setClients(clients);
        userTargetingSegmentsRequest.setUserIds(allUids);
        userTargetingSegmentsRequest.setDeviceId(Utils.getDeviceId(deviceIdHeader));
        Map<String, String> response = userTargetingService.getUserTargetingSegments(userTargetingSegmentsRequest, AdConstants.SEGMENTED_PRICING_CONSIDERATION,
                ContollerType.S2S);
        AnalyticService.update("response", response.toString());
        return ResponseEntity.ok(response);
    }
}
