package in.wynk.subscription.service.impl;

import in.wynk.subscription.core.dao.entity.Plan;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;
import in.wynk.subscription.core.service.IUserdataService;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.dto.SubscriptionStatus;
import in.wynk.subscription.dto.request.ActiveProductsComputationRequest;
import in.wynk.subscription.dto.response.ActiveProductsComputationResponse;
import in.wynk.subscription.service.ISubscriptionStatusService;
import in.wynk.subscription.service.IUserProductService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubscriptionStatusServiceImpl implements ISubscriptionStatusService {

    private final IUserdataService userdataService;
    private final IUserProductService userProductService;
    private final SubscriptionCachingService cachingService;

    public SubscriptionStatusServiceImpl(IUserdataService userdataService, IUserProductService userProductService, SubscriptionCachingService cachingService) {
        this.userdataService = userdataService;
        this.userProductService = userProductService;
        this.cachingService = cachingService;
    }

    @Override
    public List<SubscriptionStatus> getSubscriptionStatus(String uid, String service) {
        Map<Integer, UserPlanDetails> activeOrInGracePlanDetailsMap = userdataService.getAllUserPlanDetails(uid, service).stream().filter(UserPlanDetails::isFreeActiveOrPaidActive).collect(Collectors.toMap(UserPlanDetails::getPlanId, Function.identity()));
        Set<Plan> activeOrInGracePlans = activeOrInGracePlanDetailsMap.keySet().stream().filter(cachingService::containsPlan).map(cachingService::getPlan).collect(Collectors.toSet());
        ActiveProductsComputationResponse computationResponse = userProductService.compute(ActiveProductsComputationRequest.builder().activePlans(activeOrInGracePlans).build());
        return computationResponse.getActivePlanProductMap().entrySet().stream().flatMap(entry -> entry.getValue().stream().map(p -> Pair.of(activeOrInGracePlanDetailsMap.get(entry.getKey()), p))).map(SubscriptionStatus::new).collect(Collectors.toList());
    }

}
