package in.wynk.targeting.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.targeting.core.constant.AdConstants;
import in.wynk.targeting.core.utils.Utils;
import in.wynk.targeting.services.AdTargetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("musicTargetingController")
@RequestMapping("/music/")
public class TargetingController {

    @Autowired
    private AdTargetingService adTargetingService;

    @GetMapping({"/v1/usertargeting", "v2/usertargeting", "v4/usertargeting"})
    @AnalyseTransaction(name = "musicUserTargeting")
    public Map<String, String> getUserTargetingSegments(@RequestHeader(AdConstants.MUSIC_HEADER_DID) String deviceIdHeader,
                                                        @RequestHeader(AdConstants.UTKN_HEADER) String utknHeader) {
        String uid = Utils.getUidFromHeader(utknHeader);
        AnalyticService.update("uid", uid);
        String deviceId = Utils.getDeviceId(deviceIdHeader);
        Map<String, String> response = adTargetingService.getUserSegments(uid, deviceId);
        AnalyticService.update("response", response.toString());
        return response;
    }
}
