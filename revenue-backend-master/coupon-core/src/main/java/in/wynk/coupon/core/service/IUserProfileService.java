package in.wynk.coupon.core.service;

import java.util.List;
import java.util.Map;

public interface IUserProfileService {

     List<Integer> fetchActivePlans(String uid, String service);

     Map<String, List<String>> fetchThanksSegment(String msisdn, String service);

}
