package in.wynk.subscription.service;

import in.wynk.common.enums.WynkService;
import in.wynk.subscription.common.dto.ActivePlansResponse;
import in.wynk.subscription.common.dto.ThanksSegmentResponse;
import in.wynk.subscription.common.dto.UserProfile;

public interface IUserProfileService {

    ActivePlansResponse fetchActivePlans(String uid, WynkService service);

    ThanksSegmentResponse fetchAllThanksSegments(String uid, WynkService service);

    UserProfile fetchUserProfile(String uid, WynkService service);

}
