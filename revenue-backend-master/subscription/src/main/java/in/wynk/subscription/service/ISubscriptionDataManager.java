package in.wynk.subscription.service;

import in.wynk.partner.common.dto.PartnerEligiblePlansResponse;
import in.wynk.subscription.common.dto.PlanDTO;
import in.wynk.subscription.core.dao.entity.Offer;
import in.wynk.subscription.core.dao.entity.Partner;
import in.wynk.subscription.core.dao.entity.Product;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ISubscriptionDataManager {

    List<PlanDTO> allPlans(String service);

    Map<String, Collection<Product>> allProducts(String service);

    Map<String, Collection<Partner>> allPartners(String service);

    Map<String, Collection<Offer>> allOffers(String service);

    PartnerEligiblePlansResponse getPlansToBeListedForPartner(String partnerName, String service);
}
