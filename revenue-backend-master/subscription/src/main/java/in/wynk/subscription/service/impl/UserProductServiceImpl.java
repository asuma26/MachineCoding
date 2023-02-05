package in.wynk.subscription.service.impl;

import in.wynk.subscription.common.enums.PlanType;
import in.wynk.subscription.core.constants.SubscriptionLoggingMarkers;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.dto.OfferPlanProductsForProvision;
import in.wynk.subscription.dto.request.ActiveProductsComputationRequest;
import in.wynk.subscription.dto.request.EligibleProductsComputationRequest;
import in.wynk.subscription.dto.request.ProductsComputationRequest;
import in.wynk.subscription.dto.response.ActiveProductsComputationResponse;
import in.wynk.subscription.dto.response.EligibleProductsComputationResponse;
import in.wynk.subscription.dto.response.ProductsComputationResponse;
import in.wynk.subscription.service.IUserProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static in.wynk.common.constant.BaseConstants.OFFER_ID;

@Slf4j
@Service
public class UserProductServiceImpl implements IUserProductService {

    private final SubscriptionCachingService cachingService;

    public UserProductServiceImpl(SubscriptionCachingService cachingService) {
        this.cachingService = cachingService;
    }

    private boolean isProductEligible(Product product, Offer offer, Map<String, Product> activeProductMap, Map<String, Offer> activeOffersMap) {
        if (!activeProductMap.containsKey(product.getPackGroup()) || !activeOffersMap.containsKey(product.getPackGroup())) {
            return true;
        }
        if (activeProductMap.get(product.getPackGroup()).getHierarchy() < product.getHierarchy()) {
            return true;
        } else if (activeProductMap.get(product.getPackGroup()).getHierarchy() == product.getHierarchy()) {
            return activeOffersMap.containsKey(product.getPackGroup()) && activeOffersMap.get(product.getPackGroup()).getHierarchy() <= offer.getHierarchy();
        }
        return false;
    }

    @Override
    @Deprecated
    public OfferPlanProductsForProvision getProductToBeProvisioned(Set<Plan> plansToBeSubscribed, List<Subscription> activeSubscriptions, List<UserPlanDetails> activePlans) {
        Map<String, Product> activeProductsMap = activeSubscriptions.stream().map(Subscription::getProductId).filter(cachingService::containsProduct)
                .map(cachingService::getProduct).collect(Collectors.toMap(Product::getPackGroup, Function.identity()));
        Map<String, Offer> activeOffersMap = new HashMap<>();
        activeSubscriptions.forEach(s -> {
            String offerIdStr = s.getPaymentMetaData().get(OFFER_ID);
            if (cachingService.containsOffer(offerIdStr)) {
                activeOffersMap.put(s.getPackGroup(), cachingService.getOffer(offerIdStr));
            }
        });
        Map<String, Product> productToBeProvisionedMap = new HashMap<>();
        Map<String, Offer> productOfferMap = new HashMap<>();
        Map<String, Plan> productPlanMap = new HashMap<>();
        for (Plan plan : plansToBeSubscribed) {
            Offer o = cachingService.getOffer(plan.getLinkedOfferId());
            List<Product> products = o.getProducts().keySet().stream().map(cachingService::getProduct).filter(a -> isProductEligible(a, o, activeProductsMap, activeOffersMap)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(products)) {
                products.forEach(p -> {
                    if (productToBeProvisionedMap.containsKey(p.getPackGroup()) && productToBeProvisionedMap.get(p.getPackGroup()).getHierarchy() <= p.getHierarchy()) {
                        productToBeProvisionedMap.put(p.getPackGroup(), p);
                    } else if (!productToBeProvisionedMap.containsKey(p.getPackGroup())) {
                        productToBeProvisionedMap.put(p.getPackGroup(), p);
                    }

                    if (productOfferMap.containsKey(p.getPackGroup()) && productOfferMap.get(p.getPackGroup()).getHierarchy() <= o.getHierarchy()) {
                        productOfferMap.put(p.getPackGroup(), o);
                        productPlanMap.put(p.getPackGroup(), plan);
                    } else if (!productOfferMap.containsKey(p.getPackGroup())) {
                        productOfferMap.put(p.getPackGroup(), o);
                        productPlanMap.put(p.getPackGroup(), plan);
                    }
                });
            }
        }
        Set<Offer> offers = new HashSet<>(productOfferMap.values());
        Map<Plan, Set<Product>> planProductMap = new HashMap<>();
        //check for any remaining product from active paid plans.
        Map<Plan, Set<Product>> activePaidPlanProductMap = new HashMap<>();
        List<Plan> activePaidPlans = activePlans.stream().filter(UserPlanDetails::isFreeActiveOrPaidActive).map(p -> cachingService.getPlan(p.getPlanId())).filter(p -> p.getType() != PlanType.FREE).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(activePaidPlans)) {
            for (Plan plan : activePaidPlans) {
                Offer offer = cachingService.getOffer(plan.getLinkedOfferId());
                Set<Product> eligiblePaidProducts = offer.getProducts().keySet().stream().map(cachingService::getProduct)
                        .filter(p -> {
                            if (!activeProductsMap.containsKey(p.getPackGroup()) && !productToBeProvisionedMap.containsKey(p.getPackGroup())) {
                                return true;
                            }
                            if (productToBeProvisionedMap.containsKey(p.getPackGroup()) && productToBeProvisionedMap.get(p.getPackGroup()).getHierarchy() < p.getHierarchy()) {
                                productToBeProvisionedMap.remove(p.getPackGroup());
                                productPlanMap.remove(p.getPackGroup());
                                return true;
                            } else if (productToBeProvisionedMap.containsKey(p.getPackGroup()) && productToBeProvisionedMap.get(p.getPackGroup()).getHierarchy() >= p.getHierarchy()) {
                                return false;
                            }
                            return activeProductsMap.get(p.getPackGroup()).getHierarchy() < p.getHierarchy();
                        })
//                            .filter(p->isProductEligible(p, offer, activeProductsMap, activeOffersMap))
                        .collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(eligiblePaidProducts)) {
                    activePaidPlanProductMap.put(plan, eligiblePaidProducts);
                }
            }
        }
        for (Map.Entry<String, Plan> e : productPlanMap.entrySet()) {
            Set<Product> planProduct = planProductMap.getOrDefault(e.getValue(), new HashSet<>());
            planProduct.add(productToBeProvisionedMap.get(e.getKey()));
            planProductMap.put(e.getValue(), planProduct);
        }
        return OfferPlanProductsForProvision.builder().planProductMap(planProductMap).activePaidPlanProductMap(activePaidPlanProductMap).products(new HashSet<>(productToBeProvisionedMap.values()))
                .offers(offers).plans(new HashSet<>(productPlanMap.values())).build();
    }

    @Override
    public <T extends ProductsComputationResponse, R extends ProductsComputationRequest> T compute(R request) {
        final ProductsComputationResponse response;
        if (request instanceof ActiveProductsComputationRequest) {
            response = compute((ActiveProductsComputationRequest) request);
        } else {
            response = compute((EligibleProductsComputationRequest) request);
        }
        return (T) response;
    }

    private ActiveProductsComputationResponse compute(ActiveProductsComputationRequest request) {
        Map<Integer, Set<Product>> activePlanProductsMap = internalCompute(request.getActivePlans(), (pair) -> true);
        return ActiveProductsComputationResponse.builder().activePlanProductMap(activePlanProductsMap).build();
    }

    private EligibleProductsComputationResponse compute(EligibleProductsComputationRequest request) {
        ActiveProductsComputationResponse activeResponse = compute(ActiveProductsComputationRequest.builder().activePlans(request.getActivePlans()).build());
        Map<String, Pair<Offer, Product>> activePackGroupToOfferProductPairMap = activeResponse.getActivePlanProductMap().entrySet().stream()
                .filter(entry -> cachingService.containsPlan(entry.getKey()))
                .map(entry -> entry.getValue()
                        .stream()
                        .map(product -> Pair.of(cachingService.getPlan(entry.getKey()), product))
                        .filter(pair -> cachingService.containsOffer(pair.getFirst().getLinkedOfferId()))
                        .map(pair -> Pair.of(cachingService.getOffer(pair.getFirst().getLinkedOfferId()), pair.getSecond()))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(pair -> pair.getSecond().getPackGroup(), Function.identity()));
        Map<Integer, Set<Product>> eligiblePlanProductsMap = internalCompute(request.getPlansToBeSubscribed(), (pair) -> isProductEligible(pair, activePackGroupToOfferProductPairMap));
        Map<String, Pair<Integer, Product>> eligiblePackGroupToPlanProductsMap = getPackGroupToPlanProductsMap(eligiblePlanProductsMap);
        Map<String, Pair<Integer, Product>> activePackGroupToPlanProductsMap = getPackGroupToPlanProductsMap(activeResponse.getActivePlanProductMap());
        Map<String, Pair<Integer, Product>> combinedPackGroupToPlanProductsMap = new HashMap<>(activePackGroupToPlanProductsMap);
        combinedPackGroupToPlanProductsMap.putAll(eligiblePackGroupToPlanProductsMap);
        Map<Integer, Set<Product>> combinedPlanProductsMap = combinedPackGroupToPlanProductsMap.entrySet().stream().collect(Collectors.groupingBy(entry -> entry.getValue().getFirst(), Collectors.mapping(entry -> entry.getValue().getSecond(), Collectors.toSet())));
        return EligibleProductsComputationResponse.builder().eligiblePlanProductMap(eligiblePlanProductsMap).combinedPlanProductMap(combinedPlanProductsMap).build();
    }

    private Map<Integer, Set<Product>> internalCompute(Set<Plan> computablePlans, Predicate<Pair<Offer, Product>> filterProductsPredicate) {
        final Map<String, Plan> productPlanMap = new HashMap<>();
        final Map<String, Offer> productOfferMap = new HashMap<>();
        final Map<String, Product> productToBeProvisionedMap = new HashMap<>();

        for (Plan computablePlan : computablePlans) {
            if (cachingService.containsOffer(computablePlan.getLinkedOfferId())) {
                Offer offerToBeSubscribed = cachingService.getOffer(computablePlan.getLinkedOfferId());
                List<Product> filteredProducts = offerToBeSubscribed.getProducts().keySet().stream().filter(cachingService::containsProduct).map(pid -> Pair.of(offerToBeSubscribed, cachingService.getProduct(pid))).filter(filterProductsPredicate).map(Pair::getSecond).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(filteredProducts)) {
                    filteredProducts.forEach(p -> {
                        if (productToBeProvisionedMap.containsKey(p.getPackGroup()) && productToBeProvisionedMap.get(p.getPackGroup()).getHierarchy() < p.getHierarchy()) {
                            productToBeProvisionedMap.put(p.getPackGroup(), p);
                            productOfferMap.put(p.getPackGroup(), offerToBeSubscribed);
                            productPlanMap.put(p.getPackGroup(), computablePlan);
                        } else if (!productToBeProvisionedMap.containsKey(p.getPackGroup())) {
                            productToBeProvisionedMap.put(p.getPackGroup(), p);
                            productOfferMap.put(p.getPackGroup(), offerToBeSubscribed);
                            productPlanMap.put(p.getPackGroup(), computablePlan);
                        } else if (productToBeProvisionedMap.get(p.getPackGroup()).getHierarchy() == p.getHierarchy() &&
                                        productOfferMap.containsKey(p.getPackGroup()) && productOfferMap.get(p.getPackGroup()).getHierarchy() <= offerToBeSubscribed.getHierarchy()) {
                            productOfferMap.put(p.getPackGroup(), offerToBeSubscribed);
                            productPlanMap.put(p.getPackGroup(), computablePlan);
                        }
                    });
                }
            } else {
                log.error(SubscriptionLoggingMarkers.PRODUCT_COMPUTATION_ERROR, "offer id {} linked with plan id: {} is not found in cache", computablePlan.getLinkedOfferId(), computablePlan.getId());
            }
        }

        return productPlanMap.entrySet().stream()
                .filter(entry -> productToBeProvisionedMap.containsKey(entry.getKey()))
                .map(entry -> Pair.of(entry.getValue(), productToBeProvisionedMap.get(entry.getKey())))
                .collect(Collectors.groupingBy(pair -> pair.getFirst().getId(), Collectors.mapping(Pair::getSecond, Collectors.toSet())));
    }

    private boolean isProductEligible(Pair<Offer, Product> offerProductPairToBeSubscribed, Map<String, Pair<Offer, Product>> activePackGroupToOfferProductPairMap) {
        Offer offerToBeSubscribed = offerProductPairToBeSubscribed.getFirst();
        Product productToBeSubscribed = offerProductPairToBeSubscribed.getSecond();
        if (!activePackGroupToOfferProductPairMap.containsKey(productToBeSubscribed.getPackGroup())) {
            return true;
        }
        if (activePackGroupToOfferProductPairMap.get(productToBeSubscribed.getPackGroup()).getSecond().getHierarchy() < productToBeSubscribed.getHierarchy()) {
            return true;
        } else if (activePackGroupToOfferProductPairMap.get(productToBeSubscribed.getPackGroup()).getSecond().getHierarchy() == productToBeSubscribed.getHierarchy()) {
            return activePackGroupToOfferProductPairMap.containsKey(productToBeSubscribed.getPackGroup()) && activePackGroupToOfferProductPairMap.get(productToBeSubscribed.getPackGroup()).getFirst().getHierarchy() <= offerToBeSubscribed.getHierarchy();
        }
        return false;
    }

    private Map<String, Pair<Integer, Product>> getPackGroupToPlanProductsMap(Map<Integer, Set<Product>> planProductsMap) {
        return planProductsMap.entrySet().stream().map(entry -> entry.getValue().stream().map(product -> Pair.of(entry.getKey(), product)).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toMap(pair -> pair.getSecond().getPackGroup(), Function.identity()));
    }

}
