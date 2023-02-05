package in.wynk.targeting.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.dto.EmptyResponse;
import in.wynk.subscription.common.dto.UserCachePurgeRequest;
import in.wynk.targeting.services.AdService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wynk/s2s/v1/")
public class WynkAdsS2SController {

    private final AdService adService;

    public WynkAdsS2SController(AdService adService) {
        this.adService = adService;
    }

    @PostMapping("/purge")
    @AnalyseTransaction(name = "adsPurge")
    public EmptyResponse userProfileAdsPurge(@RequestBody UserCachePurgeRequest requestDTO) {
        AnalyticService.update(requestDTO);
        adService.evictUserProfileAdCache(requestDTO);
        return EmptyResponse.response();
    }
}
