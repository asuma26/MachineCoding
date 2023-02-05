package in.wynk.subscription.service.impl;

import in.wynk.partner.common.dto.EligiblePlanDetails;
import in.wynk.partner.common.dto.PartnerEligiblePlansResponse;
import in.wynk.subscription.common.dto.PlanDTO;
import in.wynk.subscription.common.dto.PlanPeriodDTO;
import in.wynk.subscription.common.dto.PriceDTO;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.service.ISubscriptionDataManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class SubscriptionDataManager implements ISubscriptionDataManager {

    private final SubscriptionCachingService cachingService;

    public SubscriptionDataManager(SubscriptionCachingService cachingService) {
        this.cachingService = cachingService;
    }

    @Override
    public List<PlanDTO> allPlans(String service) {
        Collection<Plan> planList = cachingService.getPlans().values();
        if (StringUtils.isNotBlank(service)) {
            planList = planList.stream().filter(s -> StringUtils.equalsIgnoreCase(service, s.getService())).collect(Collectors.toList());
        }
        return planList.stream().map(this::createPlanDTO).collect(Collectors.toList());
    }

    private PlanDTO createPlanDTO(Plan plan) {
        PriceDTO priceDTO = createPriceDTO(plan.getPrice());
        PlanPeriodDTO planPeriodDTO = createPeriodDTO(plan.getPeriod());
        return PlanDTO.builder().price(priceDTO).service(plan.getService()).planType(plan.getType()).linkedOfferId(plan.getLinkedOfferId()).period(planPeriodDTO).title(plan.getTitle()).id(plan.getId()).sku(plan.getSku()).linkedFreePlanId(plan.getLinkedFreePlanId()).build();
    }

    private PriceDTO createPriceDTO(Price price) {
        return PriceDTO.builder().currency(price.getCur()).amount(price.getAmount()).monthlyAmount(price.getMonthlyAmount()).displayAmount(price.getDisplayAmount()).savings(price.getSavings()).bestPlan(price.isBestPlan()).build();
    }

    private PlanPeriodDTO createPeriodDTO(Period period) {
        return PlanPeriodDTO.builder()
                .timeUnit(period.getTimeUnit())
                .validity(period.getValidity())
                .retryInterval(period.getRetryInterval())
                .maxRetryCount(period.getMaxRetryCount())
                .validityUnit(period.getValidityUnit())
                .build();
    }

    @Override
    public Map<String, Collection<Product>> allProducts(String service) {
        Collection<Product> list = cachingService.getProducts().values();
        if (StringUtils.isNotBlank(service)) {
            list = list.stream().filter(s -> StringUtils.equalsIgnoreCase(service, s.getService())).collect(Collectors.toList());
        }
        Map<String, Collection<Product>> map = new HashMap<>();
        map.put("allProducts", list);
        return map;
    }

    @Override
    public Map<String, Collection<Partner>> allPartners(String service) {
        Collection<Partner> list = cachingService.getPartners().values();
        if (StringUtils.isNotBlank(service)) {
            list = list.stream().filter(s -> StringUtils.equalsIgnoreCase(service, s.getService())).collect(Collectors.toList());
        }
        Map<String, Collection<Partner>> map = new HashMap<>();
        map.put("allPartners", list);
        return map;
    }

    @Override
    public Map<String, Collection<Offer>> allOffers(String service) {
        Collection<Offer> list = cachingService.getOffers().values();
        if (StringUtils.isNotBlank(service)) {
            list = list.stream().filter(s -> StringUtils.equalsIgnoreCase(service, s.getService())).collect(Collectors.toList());
        }
        Map<String, Collection<Offer>> map = new HashMap<>();
        map.put("allOffers", list);
        return map;
    }

    @Override
    public PartnerEligiblePlansResponse getPlansToBeListedForPartner(String partnerName, String service) {
        Map<Integer, Plan> activePlans = cachingService.getPlans();
        List<EligiblePlanDetails> eligiblePlanDetails = new ArrayList<>();
        if(!isEmpty(activePlans)) {
            List<Plan> eligiblePlans = activePlans.values().stream()
                    .filter(plan -> org.apache.commons.lang.StringUtils.equalsIgnoreCase(plan.getService(), service))
                    .filter(plan -> plan.getEligiblePartners() != null && plan.getEligiblePartners().contains(partnerName))
                    .collect(Collectors.toList());
            for (Plan plan : eligiblePlans) {
                eligiblePlanDetails.add(EligiblePlanDetails.builder()
                        .planId(plan.getId())
                        .total(plan.getPrice().getAmount())
                        .planTitle(plan.getTitle())
                        .build());
            }
        }
        return PartnerEligiblePlansResponse.builder().eligiblePlanDetails(eligiblePlanDetails).build();
    }

}
