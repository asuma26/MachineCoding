package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.subscription.dto.OfferProvisionRequest;
import in.wynk.subscription.dto.response.OfferProvisionResponse;
import in.wynk.subscription.service.IOfferProvisionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Abhishek
 * @created 17/06/20
 */
@RestController
@RequestMapping(value = "/wynk/s2s/v1/offer")
public class OfferRequestController {

    @Autowired
    private IOfferProvisionService offerProcessingService;

    @ApiOperation("Api to provision offers basis eligibility. Similar to old /offer/get/provision API")
    @AnalyseTransaction(name = "offerProvision")
    @PostMapping(value = "/provision")
    public OfferProvisionResponse provisionOffers(@RequestBody OfferProvisionRequest offerProvisionRequest) {
        AnalyticService.update(offerProvisionRequest);
        OfferProvisionResponse.OfferProvisionData response = offerProcessingService.provisionOffers(offerProvisionRequest);
        AnalyticService.update(response);
        return OfferProvisionResponse.builder().data(response).build();
    }
}
