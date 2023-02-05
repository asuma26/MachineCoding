package in.wynk.utils.controller.brandChannels;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.dto.WynkResponse;
import in.wynk.utils.request.BrandChannelRequest;
import in.wynk.utils.service.brandChannel.BrandChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wynk/s2s/v1/brandChannel")
public class BrandChannelController {

    @Autowired
    private BrandChannelService brandChannelService;

    @PutMapping("/create")
    @AnalyseTransaction(name = "createBrandChannel")
    public WynkResponse<String> exportDumpOnS3(@RequestBody BrandChannelRequest brandChannelRequest) {
        AnalyticService.update(brandChannelRequest);
        String responseUrl = brandChannelService.storeBrandChannelS3(brandChannelRequest);
        WynkResponse<String> response = WynkResponse.<String>builder().body(responseUrl).build();
        AnalyticService.update(responseUrl);
        return response;
    }
}
