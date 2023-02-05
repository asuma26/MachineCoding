package in.wynk.subscription.controller;

import com.github.annotation.analytic.core.annotations.AnalyseTransaction;
import com.github.annotation.analytic.core.service.AnalyticService;
import in.wynk.common.dto.WynkResponse;
import in.wynk.common.enums.WynkService;
import in.wynk.common.utils.MsisdnUtils;
import in.wynk.exception.WynkErrorType;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.subscription.common.dto.ActivePlansResponse;
import in.wynk.subscription.common.dto.ThanksSegmentResponse;
import in.wynk.subscription.common.dto.UserProfile;
import in.wynk.subscription.common.dto.UserProfileRequest;
import in.wynk.subscription.service.IUserProfileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import static in.wynk.common.constant.BaseConstants.*;

@RestController
@RequestMapping("/wynk/s2s/v1/user")
public class UserProfileController {

    private final IUserProfileService userProfileService;

    public UserProfileController(IUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/segments")
    @AnalyseTransaction(name = "userActiveSegment")
    public WynkResponse<ThanksSegmentResponse> fetchThanksSegment(@RequestParam String msisdn, @RequestParam String service) {
        msisdn = MsisdnUtils.normalizePhoneNumber(msisdn);
        if (StringUtils.isBlank(msisdn)) {
            throw new WynkRuntimeException(WynkErrorType.UT001, "Invalid UID of MSISDN");

        }
        AnalyticService.update(MSISDN, msisdn);
        AnalyticService.update(SERVICE, service);
        ThanksSegmentResponse data = userProfileService.fetchAllThanksSegments(msisdn, WynkService.fromString(service));
        AnalyticService.update(data);
        return WynkResponse.<ThanksSegmentResponse>builder().body(data).build();
    }

    @GetMapping("/active/plans")
    @AnalyseTransaction(name = "userActivePlan")
    public WynkResponse<ActivePlansResponse> fetchActivePlans(@RequestParam String uid, @RequestParam String service) {
        AnalyticService.update(UID, uid);
        AnalyticService.update(SERVICE, service);
        ActivePlansResponse activePlansResponse = userProfileService.fetchActivePlans(uid, WynkService.fromString(service));
        AnalyticService.update(activePlansResponse);
        return WynkResponse.<ActivePlansResponse>builder().body(activePlansResponse).build();
    }

    @PostMapping("/profile")
    @AnalyseTransaction(name = "userProfile")
    public UserProfile fetchUserProfile(@RequestBody UserProfileRequest userRequest) {
        AnalyticService.update(userRequest);
        UserProfile userPlanProfile = userProfileService.fetchUserProfile(userRequest.getUid(), WynkService.fromString(userRequest.getService()));
        AnalyticService.update(userPlanProfile);
        return userPlanProfile;
    }


}
