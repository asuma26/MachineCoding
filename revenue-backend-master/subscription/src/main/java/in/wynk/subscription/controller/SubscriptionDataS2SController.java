package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.constant.BaseConstants;
import in.wynk.common.dto.WynkResponse;
import in.wynk.partner.common.dto.PartnerEligiblePlansResponse;
import in.wynk.subscription.common.dto.AllPlansResponse;
import in.wynk.subscription.core.dao.entity.Offer;
import in.wynk.subscription.core.dao.entity.Partner;
import in.wynk.subscription.core.dao.entity.Product;
import in.wynk.subscription.service.ISubscriptionDataManager;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/wynk/s2s/v1/data/")
public class SubscriptionDataS2SController {

    private final ISubscriptionDataManager dataManager;

    public SubscriptionDataS2SController(ISubscriptionDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @ApiOperation("Provides a list of active plans. \nAn alternate to old /allPacks API with combination of /allProducts")
    @GetMapping("/allPlans")
    @AnalyseTransaction(name = "allPlans")
    public WynkResponse<AllPlansResponse> allPlans(@RequestParam(required = false) String service) {
        AnalyticService.update(BaseConstants.SERVICE, service);
        return WynkResponse.<AllPlansResponse>builder().body(AllPlansResponse.builder().plans(dataManager.allPlans(service)).build()).build();
    }

    @ApiOperation("Provides a list of active products. \nAn alternate to old /allPacks API with combination of /allPlans")
    @GetMapping("/allProducts")
    @AnalyseTransaction(name = "allProducts")
    public WynkResponse<Map<String, Collection<Product>>> allProducts(@RequestParam(required = false) String service) {
        AnalyticService.update(BaseConstants.SERVICE, service);
        return WynkResponse.<Map<String, Collection<Product>>>builder().body(dataManager.allProducts(service)).build();
    }

    @ApiOperation("Provides a list of all active partners")
    @GetMapping("/allPartners")
    @AnalyseTransaction(name = "allPartners")
    public WynkResponse<Map<String, Collection<Partner>>> allPartners(@RequestParam(required = false) String service) {
        AnalyticService.update(BaseConstants.SERVICE, service);
        return WynkResponse.<Map<String, Collection<Partner>>>builder().body(dataManager.allPartners(service)).build();
    }

    @ApiOperation("Provides a list of active offers. \nAn alternate to old /allOffers API")
    @GetMapping("/allOffers")
    @AnalyseTransaction(name = "allOffers")
    public WynkResponse<Map<String, Collection<Offer>>> allOffers(@RequestParam(required = false) String service) {
        AnalyticService.update(BaseConstants.SERVICE, service);
        return WynkResponse.<Map<String, Collection<Offer>>>builder().body(dataManager.allOffers(service)).build();
    }

    @GetMapping(value = "/partner/plans")
    public PartnerEligiblePlansResponse getAllPlansToBeListed(@RequestParam String partnerName, @RequestParam String service){
        return dataManager.getPlansToBeListedForPartner(partnerName, service);
    }
}
