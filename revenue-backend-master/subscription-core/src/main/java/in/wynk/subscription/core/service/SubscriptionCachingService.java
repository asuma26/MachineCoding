package in.wynk.subscription.core.service;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.enums.WynkService;
import in.wynk.data.enums.State;
import in.wynk.spel.IExpressionParser;
import in.wynk.subscription.common.enums.ProvisionType;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.dao.repository.sedb.SubscriptionPackDao;
import in.wynk.subscription.core.dao.repository.subscription.*;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import static in.wynk.common.constant.BaseConstants.IN_MEMORY_CACHE_CRON;
import static in.wynk.logging.BaseLoggingMarkers.APPLICATION_ERROR;
import static in.wynk.subscription.common.enums.ProvisionType.FREE;
import static in.wynk.subscription.common.enums.ProvisionType.PAID;

@Service
@Getter
public class SubscriptionCachingService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionCachingService.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final Map<Integer, Offer> offers = new ConcurrentHashMap<>();
    private final Map<ProvisionType, List<Offer>> bucketedOffers = new ConcurrentHashMap<>();
    private final Map<Integer, Plan> plans = new ConcurrentHashMap<>();
    private final Map<Integer, List<Plan>> offerPlans = new ConcurrentHashMap<>();
    private final Map<Integer, Product> products = new ConcurrentHashMap<>();
    private final Map<String, List<Offer>> relatedOffers = new ConcurrentHashMap<>();
    private final Map<String, Partner> partners = new ConcurrentHashMap<>();
    private final Map<WynkService, List<Offer>> serviceOffers = new ConcurrentHashMap<>();
    private final Map<Integer, Expression> offerRuleExpressions = new ConcurrentHashMap<>();
    private final Map<Integer, Expression> planRuleExpressions = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> oldPackToNewPlanMapping = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> oldFreePackToNewPlanMapping = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> oldFMFToNewPlanMapping = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, OfferPlanMapping>> oldOfferIdToNewOfferPlanMapping = new ConcurrentHashMap<>();
    private final Map<Integer, String> subscriptionPackMap = new ConcurrentHashMap<>();
    private final Map<String, IngressIntentMap> ingressIntentMapMap = new ConcurrentHashMap<>();
    private final Map<String, ThanksSegment> thanksSegmentMap = new ConcurrentHashMap<>();
    private final ThanksSegmentDao thanksSegmentDao;
    private final OfferDao offerDao;
    private final PlanDao planDao;
    private final ProductDao productDao;
    private final PartnerDao partnerDao;
    private final OfferMapDao offerMapDao;
    private final IngressIntentMapDao ingressIntentMapDao;
    private final SubscriptionPackDao subscriptionPackDao;
    private final IExpressionParser expressionParser;
    private Long lastSuccessfulOfferCacheTime;

    public SubscriptionCachingService(ThanksSegmentDao thanksSegmentDao, OfferDao offerDao, PlanDao planDao, ProductDao productDao, PartnerDao partnerDao, OfferMapDao offerMapDao, IngressIntentMapDao ingressIntentMapDao, SubscriptionPackDao subscriptionPackDao, IExpressionParser expressionParser) {
        this.thanksSegmentDao = thanksSegmentDao;
        this.planDao = planDao;
        this.offerDao = offerDao;
        this.productDao = productDao;
        this.partnerDao = partnerDao;
        this.offerMapDao = offerMapDao;
        this.ingressIntentMapDao = ingressIntentMapDao;
        this.subscriptionPackDao = subscriptionPackDao;
        this.expressionParser = expressionParser;
        loadSubscriptionPack();
        init();
    }

    @Scheduled(fixedDelay = IN_MEMORY_CACHE_CRON, initialDelay = IN_MEMORY_CACHE_CRON)
    @AnalyseTransaction(name = "refreshSubscriptionPack")
    public void loadSubscriptionPack() {
        AnalyticService.update("class", this.getClass().getSimpleName());
        AnalyticService.update("loadSubscriptionPackInit", true);
        subscriptionPackMap.putAll(subscriptionPackDao.findAll().parallelStream().collect(Collectors.toMap(SubscriptionPack::getId, SubscriptionPack::getPaymentMethod)));
        AnalyticService.update("loadSubscriptionPackCompleted", true);
    }

    @Scheduled(fixedDelay = IN_MEMORY_CACHE_CRON, initialDelay = IN_MEMORY_CACHE_CRON)
    @AnalyseTransaction(name = "refreshInMemoryCache")
    public void init() {
        AnalyticService.update("class", this.getClass().getSimpleName());
        AnalyticService.update("cacheLoadInit", true);
        loadPartners();
        loadOffers();
        loadPlans();
        loadProducts();
        loadOfferMap();
        loadIngressIntentMap();
        loadThanksSegmentMap();
        AnalyticService.update("cacheLoadCompleted", true);
    }

    private void loadThanksSegmentMap() {
        List<ThanksSegment> activeThanksSegments = thanksSegmentDao.findAllByState(State.ACTIVE);
        if (CollectionUtils.isNotEmpty(activeThanksSegments) && writeLock.tryLock()) {
            try {
                thanksSegmentMap.putAll(activeThanksSegments.stream().collect(Collectors.toMap(ThanksSegment::getId, Function.identity())));
            } catch (Throwable th) {
                logger.error(APPLICATION_ERROR, "Exception occurred while refreshing thanks segment cache. Exception: {}", th.getMessage(), th);
                throw th;
            } finally {
                writeLock.unlock();
            }
        }
    }

    private void loadIngressIntentMap() {
        List<IngressIntentMap> activeIngressIntentMap = getActiveIngressIntentMap();
        if (CollectionUtils.isNotEmpty(activeIngressIntentMap) && writeLock.tryLock()) {
            try {
                Map<String, IngressIntentMap> temp = activeIngressIntentMap.stream().collect(Collectors.toMap(IngressIntentMap::getId, Function.identity()));
                ingressIntentMapMap.putAll(temp);
            } catch (Throwable th) {
                logger.error(APPLICATION_ERROR, "Exception occurred while refreshing ingress intent config cache. Exception: {}", th.getMessage(), th);
                throw th;
            } finally {
                writeLock.unlock();
            }
        }
    }

    private List<IngressIntentMap> getActiveIngressIntentMap() {
        return ingressIntentMapDao.findAllByState(State.ACTIVE);
    }

    private void loadOffers() {
        List<Offer> activeOffers = getActiveOffers();
        if (CollectionUtils.isNotEmpty(activeOffers) && writeLock.tryLock()) {
            Map<Integer, Offer> activeOfferMap = new ConcurrentHashMap<>();
            Map<String, List<Offer>> relatedOffersMap = new ConcurrentHashMap<>();
            Map<ProvisionType, List<Offer>> bucketedOffersMap = new ConcurrentHashMap<>();
            Map<WynkService, List<Offer>> wynkServiceOffersMap = new ConcurrentHashMap<>();
            Map<Integer, Expression> offerRuleExpressionMap = new ConcurrentHashMap<>();
            logger.info("Refreshing cache for offers...");
            try {
                ExpressionParser parser = new SpelExpressionParser();
                for (Offer offer : activeOffers) {
                    Set<String> productsToRemove = new HashSet<>();
                    String packGroup = offer.getPackGroup();
                    if (packGroup != null) {
                        if (!partners.containsKey(packGroup)) {
                            productsToRemove.add(packGroup);
                        }
                        List<Offer> relatedOffers = relatedOffersMap.getOrDefault(packGroup, new ArrayList<>());
                        relatedOffers.add(offer);
                        relatedOffersMap.put(packGroup, relatedOffers);
                    }
                    //remove all disabled packs from offer.
                    for (String key : productsToRemove) {
                        offer.getProducts().remove(key);
                    }
                    if (MapUtils.isEmpty(offer.getProducts())) {
                        continue;
                    }
                    logger.debug("Building cache for offerId: {}", offer.getId());
                    activeOfferMap.put(offer.getId(), offer);
                    if (offer.getProvisionType().equals(FREE)) {
                        List<Offer> freeOffers = bucketedOffersMap.getOrDefault(FREE, new ArrayList<>());
                        freeOffers.add(offer);
                        bucketedOffersMap.put(FREE, freeOffers);
                    } else {
                        List<Offer> paidOffers = bucketedOffersMap.getOrDefault(FREE, new ArrayList<>());
                        paidOffers.add(offer);
                        bucketedOffersMap.put(PAID, paidOffers);
                    }
                    String expressionStr = "@offerCheckEligibility.init(#offerContext)";
                    if (StringUtils.isNotBlank(offer.getRuleExpression())) {
                        expressionStr = expressionStr + "." + offer.getRuleExpression();
                    }
                    Expression ruleExpression = parser.parseExpression(expressionStr);
                    offerRuleExpressionMap.put(offer.getId(), ruleExpression);

                    WynkService wynkService = WynkService.fromString(offer.getService());
                    List<Offer> serviceOffers = wynkServiceOffersMap.getOrDefault(wynkService, new ArrayList<>());
                    serviceOffers.add(offer);
                    wynkServiceOffersMap.put(wynkService, serviceOffers);
                }
                this.relatedOffers.clear();
                this.relatedOffers.putAll(relatedOffersMap);
                this.bucketedOffers.clear();
                this.bucketedOffers.putAll(bucketedOffersMap);
                this.serviceOffers.clear();
                this.serviceOffers.putAll(wynkServiceOffersMap);
                this.offers.putAll(activeOfferMap);
                this.offerRuleExpressions.putAll(offerRuleExpressionMap);
                lastSuccessfulOfferCacheTime = System.currentTimeMillis();
                logger.info("I have build up my cache, will take a power nap again");
            } catch (Throwable th) {
                logger.error(APPLICATION_ERROR, "Exception occurred while refreshing offer config cache. Exception: {}", th.getMessage(), th);
                throw th;
            } finally {
                writeLock.unlock();
            }
        }
    }

    private void loadPlans() {
        List<Plan> activePlans = getActivePlans();
        if (CollectionUtils.isNotEmpty(activePlans) && writeLock.tryLock()) {
            try {
                Map<Integer, List<Plan>> offerPlansMap = new ConcurrentHashMap<>();
                Map<Integer, Expression> planRuleExpressionMap = new ConcurrentHashMap<>();
                Map<Integer, Plan> activePlanMap = activePlans.stream().collect(Collectors.toMap(Plan::getId, Function.identity()));
                for (Plan plan : activePlans) {
                    List<Plan> plans = offerPlansMap.getOrDefault(plan.getLinkedOfferId(), new ArrayList<>());
                    if (containsOffer(plan.getLinkedOfferId()) && getOffer(plan.getLinkedOfferId()).getPlans().contains(plan.getId())) {
                        plans.add(plan);
                        plans = plans.stream().sorted(Comparator.comparing(Plan::getHierarchy).reversed()).collect(Collectors.toList());
                        offerPlansMap.put(plan.getLinkedOfferId(), plans);
                    }
                    if (CollectionUtils.isNotEmpty(plan.getLinkedOldProductIds())) {
                        for (Integer oldProductId : plan.getLinkedOldProductIds())
                            oldPackToNewPlanMapping.put(oldProductId, plan.getId());
                    }
                    if (CollectionUtils.isNotEmpty(plan.getLinkedOldFreeProductIds())) {
                        for (Integer oldFreeProductId : plan.getLinkedOldFreeProductIds()) {
                            oldFreePackToNewPlanMapping.put(oldFreeProductId, plan.getId());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(plan.getLinkedOldFMFProductIds())) {
                        for (Integer fmfProductId : plan.getLinkedOldFMFProductIds()) {
                            oldFMFToNewPlanMapping.put(fmfProductId, plan.getId());
                        }
                    }
                    String expressionStr = "init()";
                    if (StringUtils.isNotBlank(plan.getRuleExpression())) {
                        expressionStr = expressionStr + "&&" + plan.getRuleExpression();
                    }
                    planRuleExpressionMap.put(plan.getId(), expressionParser.parse(expressionStr));
                }
                this.plans.clear();
                this.plans.putAll(activePlanMap);
                this.offerPlans.clear();
                this.offerPlans.putAll(offerPlansMap);
                this.planRuleExpressions.putAll(planRuleExpressionMap);
            } catch (Throwable th) {
                logger.error(APPLICATION_ERROR, "Exception occurred while refreshing offer config cache. Exception: {}", th.getMessage(), th);
                throw th;
            } finally {
                writeLock.unlock();
            }
        }
    }

    private void loadProducts() {
        List<Product> activeProducts = getActiveProducts();
        if (CollectionUtils.isNotEmpty(activeProducts) && writeLock.tryLock()) {
            try {
                Map<Integer, Product> activePlanMap = activeProducts.stream().filter(p -> partners.containsKey(p.getPackGroup()))
                        .collect(Collectors.toMap(Product::getId, Function.identity()));
                this.products.clear();
                this.products.putAll(activePlanMap);
            } catch (Throwable th) {
                logger.error(APPLICATION_ERROR, "Exception occurred while refreshing offer config cache. Exception: {}", th.getMessage(), th);
                throw th;
            } finally {
                writeLock.unlock();
            }
        }
    }

    private void loadPartners() {
        List<Partner> activePartners = getActivePartners();
        if (CollectionUtils.isNotEmpty(activePartners) && writeLock.tryLock()) {
            try {
                Map<String, Partner> activePartnersMap = activePartners.stream().collect(Collectors.toMap(Partner::getPackGroup, Function.identity()));
                this.partners.clear();
                this.partners.putAll(activePartnersMap);
            } catch (Throwable th) {
                logger.error(APPLICATION_ERROR, "Exception occurred while refreshing offer config cache. Exception: {}", th.getMessage(), th);
                throw th;
            } finally {
                writeLock.unlock();
            }
        }
    }

    private void loadOfferMap() {
        List<OfferMap> activeOfferMap = getActiveOfferMap();
        if (CollectionUtils.isNotEmpty(activeOfferMap) && writeLock.tryLock()) {
            try {
                Map<String, Map<Integer, OfferPlanMapping>> map = activeOfferMap.stream().collect(Collectors.toMap(OfferMap::getId, OfferMap::getOfferIds));
                this.oldOfferIdToNewOfferPlanMapping.clear();
                this.oldOfferIdToNewOfferPlanMapping.putAll(map);
            } catch (Throwable throwable) {
                logger.error(APPLICATION_ERROR, "Exception occurred while refreshing offer config cache. Exception: {}", throwable.getMessage(), throwable);
                throw throwable;
            } finally {
                writeLock.unlock();
            }
        }
    }

    private List<OfferMap> getActiveOfferMap() {
        return offerMapDao.findAllByState(State.ACTIVE);
    }

    private List<Partner> getActivePartners() {
        return partnerDao.findAllByState(State.ACTIVE);
    }

    private List<Offer> getActiveOffers() {
        return offerDao.findAllByState(State.ACTIVE);
    }

    private List<Plan> getActivePlans() {
        return planDao.findAllByState(State.ACTIVE);
    }

    private List<Product> getActiveProducts() {
        return productDao.findAllByState(State.ACTIVE);
    }

    public List<Offer> getRelatedOffers(String packGroup) {
        if (StringUtils.isBlank(packGroup) || !relatedOffers.containsKey(packGroup)) {
            return Collections.emptyList();
        }
        return relatedOffers.get(packGroup);
    }

    public List<Integer> getRelatedOffersIds(String packGroup) {
        if (StringUtils.isBlank(packGroup) || !relatedOffers.containsKey(packGroup)) {
            return Collections.emptyList();
        }
        return relatedOffers.get(packGroup).stream().map(Offer::getId).collect(Collectors.toList());
    }

    public List<Offer> getServiceOffers(WynkService wynkService) {
        if (serviceOffers.containsKey(wynkService)) {
            return serviceOffers.get(wynkService);
        }
        return Collections.emptyList();
    }

    public List<Plan> getPlansForOffer(Integer offerId) {
        return offerPlans.get(offerId);
    }

    public Plan getPlan(int planId) {
        logger.trace("planId {} exists: {}", planId, plans.containsKey(planId));
        return plans.get(planId);
    }

    public Plan getPlan(String planIdStr) {
        return getPlan(NumberUtils.toInt(planIdStr));
    }

    public Partner getPartner(String packGroup) {
        return partners.get(packGroup);
    }

    public boolean containsPackGroup(String packGroup) {
        return partners.containsKey(packGroup);
    }

    public Product getProduct(Integer productId) {
        logger.trace("productId {} exists: {}", productId, products.containsKey(productId));
        return products.get(productId);
    }

    public Product getProduct(String productId) {
        return getProduct(NumberUtils.toInt(productId));
    }

    public Offer getOffer(int offerId) {
        return offers.get(offerId);
    }

    public Offer getOffer(String offerId) {
        return getOffer(NumberUtils.toInt(offerId));
    }

    public boolean containsOffer(int offerId) {
        return offers.containsKey(offerId);
    }

    public boolean containsOffer(String offerId) {
        return offers.containsKey(NumberUtils.toInt(offerId));
    }

    public boolean containsPlan(int planId) {
        return plans.containsKey(planId);
    }

    public boolean containsPlan(String planId) {
        return containsPlan(NumberUtils.toInt(planId));
    }

    public boolean containsProduct(int productId) {
        return products.containsKey(productId);
    }

    public boolean containsProduct(String productId) {
        return products.containsKey(NumberUtils.toInt(productId));
    }

    public boolean containsRelatedOffers(String packGroup) {
        return relatedOffers.containsKey(packGroup);
    }

    public boolean isFreePackEligibleForMigration(int partnerProductId) {
        return getOldFreePackToNewPlanMapping().containsKey(partnerProductId) || getOldFMFToNewPlanMapping().containsKey(partnerProductId);
    }

}
