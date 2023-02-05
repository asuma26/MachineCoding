package in.wynk.subscription.service.impl;

import in.wynk.subscription.core.dao.entity.IngressIntentMap;
import in.wynk.subscription.core.service.SubscriptionCachingService;
import in.wynk.subscription.dto.response.EligibleBenefit;
import in.wynk.subscription.service.IIngressService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static in.wynk.common.constant.BaseConstants.*;

@Service
public class IngressServiceImpl implements IIngressService<EligibleBenefit> {

    private final SubscriptionCachingService subscriptionCachingService;

    public IngressServiceImpl(SubscriptionCachingService subscriptionCachingService) {
        this.subscriptionCachingService = subscriptionCachingService;
    }

    @Override
    public void decorate(String ingressIntent, EligibleBenefit obj) {
        IngressIntentMap ingressIntentMap = subscriptionCachingService.getIngressIntentMapMap().get(ingressIntent);
        if (ingressIntentMap != null && obj != null) {
            obj.setTitle(ingressIntentMap.getTitle());
            obj.setSubtitle(ingressIntentMap.getSubtitle());
            obj.getPlans().stream().forEach(plan -> plan.setTitle(obj.getTitle()));
            Map<String, String> icons = new HashMap<>();
            icons.put(LIGHT, ingressIntentMap.getUrl());
            icons.put(DARK, ingressIntentMap.getUrl());
            Map<String, Object> eligible = new HashMap<>();
            eligible.put(ICONS, icons);
            Map<String, Object> newCard = new HashMap<>();
            newCard.put(ELIGIBLE, eligible);
            Map<String, Object> meta = obj.getMeta();
            if (meta.containsKey(CARD)) {
                Map<String, Map<String, Object>> card = (Map<String, Map<String, Object>>) meta.get(CARD);
                if (card.containsKey(ACTIVE)) {
                    newCard.put(ACTIVE, card.get(ACTIVE));
                }
            }
            Map<String, Object> newMeta = new HashMap<>();
            newMeta.putAll(obj.getMeta());
            newMeta.put(CARD, newCard);
            obj.setMeta(newMeta);
        }
    }

}
