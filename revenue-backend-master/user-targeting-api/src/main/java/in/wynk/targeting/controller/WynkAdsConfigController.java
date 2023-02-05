package in.wynk.targeting.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.targeting.core.constant.AppConstant;
import in.wynk.targeting.core.dao.entity.mongo.WynkClient;
import in.wynk.targeting.dto.DeviceMeta;
import in.wynk.targeting.dto.response.AdConfigResponse;
import in.wynk.targeting.dto.response.VideoAdConfigResponse;
import in.wynk.targeting.services.AdService;
import in.wynk.targeting.services.IWynkClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

import static in.wynk.common.constant.BaseConstants.OS;
import static in.wynk.common.constant.BaseConstants.UID;
import static in.wynk.targeting.core.constant.AdConstants.CLIENT_ID;
import static in.wynk.targeting.core.constant.AdConstants.SLOT_CONFIGS;

@RestController("adsConfig")
@RequestMapping("/wynk/ads")
public class WynkAdsConfigController {

    private static final Logger logger = LoggerFactory.getLogger(WynkAdsConfigController.class);

    private final AdService adService;
    private final IWynkClientService wynkClientService;

    public WynkAdsConfigController(AdService adService, IWynkClientService wynkClientService) {
        this.adService = adService;
        this.wynkClientService = wynkClientService;
    }

    private WynkClient getWynkClient(Principal principal, String clientId, String did) {
        AnalyticService.update(UID, principal.getName());
        WynkClient wynkClient = DeviceMeta.from(did).map(deviceMeta -> wynkClientService.lookup(clientId, deviceMeta.getOs())).get();
        AnalyticService.update(OS, wynkClient.getOs());
        AnalyticService.update(CLIENT_ID, wynkClient.getClientId());
        return wynkClient;
    }

    @GetMapping("/v1/adsConfig")
    @AnalyseTransaction(name = "adConfig")
    public AdConfigResponse getUserTargetingSegmentsV1(Principal principal, @RequestHeader(AppConstant.AUTH_CLIENT_ID) String clientId, @RequestHeader(AppConstant.AUTH_DID_HEADER) String did) throws Exception {
        AdConfigResponse response = adService.getAdConfig(principal.getName(), getWynkClient(principal, clientId, did));
        Map<String, Object> map = response.getVideoAds();
        if (map != null) {
            Map<String, VideoAdConfigResponse> oldVideoResponse = (Map<String, VideoAdConfigResponse>) map.get(SLOT_CONFIGS);
            map.clear();
            map.putAll(oldVideoResponse);
        }
        AnalyticService.update(response);
        return response;
    }

    @GetMapping("/v2/adsConfig")
    @AnalyseTransaction(name = "adConfig")
    public AdConfigResponse getUserTargetingSegmentsV2(Principal principal, @RequestHeader(AppConstant.AUTH_CLIENT_ID) String clientId, @RequestHeader(AppConstant.AUTH_DID_HEADER) String did) throws Exception {
        AdConfigResponse response = adService.getAdConfig(principal.getName(), getWynkClient(principal, clientId, did));
        AnalyticService.update(response);
        return response;
    }

    @GetMapping("/v3/adsConfig")
    @AnalyseTransaction(name = "adConfig")
    public AdConfigResponse getUserTargetingSegmentsV3(Principal principal, @RequestHeader(AppConstant.AUTH_CLIENT_ID) String clientId, @RequestHeader(AppConstant.AUTH_DID_HEADER) String did) throws Exception {
        AdConfigResponse response = adService.getAdConfigV3(principal.getName(), getWynkClient(principal, clientId, did));
        AnalyticService.update(response);
        return response;
    }

}
