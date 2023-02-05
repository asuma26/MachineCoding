package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.subscription.dto.request.MsisdnIdentificationRequest;
import in.wynk.subscription.dto.response.IdentificationResponse;
import in.wynk.subscription.service.IdentificationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Abhishek
 * @created 24/06/20
 */
@RestController
@RequestMapping(value = "/wynk/s2s/v1/msisdn")
public class MsisdnIdentificationController {

    @Autowired
    private IdentificationService identificationService;

    @ApiOperation("Provides the best msisdn by identifying the best offer." +
            "\nAn alternate API for old msisdn identification API")
    @AnalyseTransaction(name = "msisdnIdentification")
    @PostMapping(value = "/identification")
    public IdentificationResponse identifyBestMsisdn(@RequestBody MsisdnIdentificationRequest identificationRequest) throws Exception {
        AnalyticService.update(identificationRequest);
        IdentificationResponse.MsisdnIdentificationResponse response = identificationService.identifyBestMsisdn(identificationRequest);
        AnalyticService.update(response);
        return IdentificationResponse.builder().data(response).build();
    }
}
