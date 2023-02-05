package in.wynk.subscription.core.service;

import in.wynk.subscription.core.dao.entity.OfferDeviceMapping;
import in.wynk.subscription.core.dao.entity.Subscription;
import in.wynk.subscription.core.dao.entity.UserPlanDetails;

import java.util.List;

/**
 * @author Abhishek
 * @created 08/07/20
 */
public interface IUserdataService {

    void addUserPlanDetails(UserPlanDetails userPlanDetails);

    @Deprecated
    void addAllSubscriptions(String uid, String service, Iterable<Subscription> subscriptions);

    void addAllUserPlanDetails(String uid, String service, Iterable<UserPlanDetails> userPlanDetails);

    void addAllOfferDeviceMapping(String uid, String service, String deviceId, Iterable<OfferDeviceMapping> offerDeviceMappings);

    List<UserPlanDetails> getAllUserPlanDetails(String uid, String service);

    @Deprecated
    List<Subscription> getAllSubscriptions(String uid, String service);

    List<UserPlanDetails> getAllUserPlanDetails(String uid, String service, String msisdn);

    List<OfferDeviceMapping> getAllOfferDeviceMapping(String msisdn, String service, String deviceId);

}
