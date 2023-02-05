package in.wynk.subscription.service;

import in.wynk.common.enums.AppId;
import in.wynk.subscription.core.dao.entity.*;
import in.wynk.subscription.core.dao.repository.filterUsers.IFilterDbDao;
import in.wynk.subscription.core.service.UserdataService;
import in.wynk.subscription.dto.OfferContext;
import in.wynk.subscription.enums.OfferEligibilityStatus;
import in.wynk.subscription.enums.OfferEligibilityStatusReason;
import in.wynk.subscription.enums.Os;
import in.wynk.vas.client.dto.MsisdnOperatorDetails;
import in.wynk.vas.client.enums.UserType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Component
@Scope(value = "prototype")
@Slf4j
public class OfferCheckEligibility {
    private OfferEligibilityStatus status = OfferEligibilityStatus.ELIGIBLE;
    private OfferEligibilityStatusReason reason;
    private Offer offer;
    private Integer activePlanId;
    private OfferContext context;
    private boolean eligible = true;

    @Autowired
    private IFilterDbDao filterDbDao;
    @Autowired
    private UserdataService userdataService;

    public OfferCheckEligibility init(OfferContext context) {
        this.context = context;
        this.offer = context.getOffer();
        commonEligibilityCheck();
        return this;
    }

    private void commonEligibilityCheck() {
        Integer offerId = context.getOffer().getId();
        if (context.getActiveOffers() != null && context.getActiveOffers().containsKey(offerId)) {
            activePlanId = context.getActiveOffers().get(offerId);
            eligible = false;
            reason = OfferEligibilityStatusReason.ALREADY_ACTIVE;
            status = OfferEligibilityStatus.ACTIVE;
        }
    }

    public OfferCheckEligibility hasPlanActiveWithinDays(int days, Integer... planIds) {
        if (eligible) {
            List<UserPlanDetails> userPlanDetails = context.getUserPlanDetails();
            if(CollectionUtils.isEmpty(userPlanDetails)){
                reason = OfferEligibilityStatusReason.ACTIVE_PLAN_NOT_FOUND;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            Set<Integer> activePlanIds = userPlanDetails.stream()
                    .filter(details -> StringUtils.equalsIgnoreCase(context.getService().getValue(), details.getService()))
                    .filter(details -> details.getEndDate().toInstant().isAfter(ZonedDateTime.now().minusDays(days).toInstant()))
                    .map(UserPlanDetails::getPlanId)
                    .collect(Collectors.toSet());
            Optional<Integer> activePlan = Arrays.stream(planIds).filter(activePlanIds::contains).findAny();
            if(!activePlan.isPresent()){
                reason = OfferEligibilityStatusReason.ACTIVE_PLAN_NOT_FOUND;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
        }
        return this;
    }

    public OfferCheckEligibility limitDevice(int limit) {
        if (eligible) {
            if (StringUtils.isBlank(context.getDeviceId())) {
                reason = OfferEligibilityStatusReason.DEVICE_ID_REQUIRED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            List<OfferDeviceMapping> offerDeviceMappings = context.getOfferDeviceMappings();
            if(Objects.isNull(offerDeviceMappings)){
                offerDeviceMappings = userdataService.getAllOfferDeviceMapping(context.getMsisdn(), context.getService().getValue(), context.getDeviceId());
                context.setOfferDeviceMappings(offerDeviceMappings);
            }
            List<OfferDeviceMapping> selectedOfferMappings = offerDeviceMappings.stream().filter(e -> e.getOfferId() == context.getOffer().getId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(selectedOfferMappings)) {
                if (selectedOfferMappings.size() >= limit) {
                    reason = OfferEligibilityStatusReason.AVAILED_FOR_DEVICE;
                    status = OfferEligibilityStatus.NOT_ELIGIBLE;
                    eligible = false;
                    return this;
                }
            }
        }
        return this;
    }

    public OfferCheckEligibility limitUser(int limit) {
        if (eligible) {
            if (StringUtils.isBlank(context.getMsisdn())) {
                reason = OfferEligibilityStatusReason.MSISDN_REQUIRED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            int freePlanId = context.getOffer().getPlans().get(0);
            List<UserPlanDetails> userPlanDetails = context.getUserPlanDetails();
            if(CollectionUtils.isNotEmpty(userPlanDetails)) {
                List<UserPlanDetails> eligiblePlans = userPlanDetails.stream()
                        .filter(details -> details.getPlanId() == freePlanId && details.getPlanCount() >= limit)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(eligiblePlans)) {
                    reason = OfferEligibilityStatusReason.AVAILED_FOR_USER;
                    status = OfferEligibilityStatus.NOT_ELIGIBLE;
                    eligible = false;
                    return this;
                }
            }
        }
        return this;
    }

    public OfferCheckEligibility hasAvailedLinkedOffer(Integer... offerIds) {
        if (eligible) {
            if (StringUtils.isBlank(context.getMsisdn())) {
                reason = OfferEligibilityStatusReason.MSISDN_REQUIRED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            Optional<Integer> availedOfferId = getAvailedOfferId(context.getUserPlanDetails(), offerIds);
            if (!availedOfferId.isPresent()) {
                reason = OfferEligibilityStatusReason.NOT_AVAILED_LINKED_OFFER;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
        }
        return this;
    }

    private Optional<Integer> getAvailedOfferId(List<UserPlanDetails> userPlanDetails, Integer[] offerIds) {
        Set<Integer> availedOfferIds = userPlanDetails.stream().map(UserPlanDetails::getOfferId).collect(Collectors.toSet());
        return Arrays.stream(offerIds).filter(availedOfferIds::contains).findAny();
    }

    public OfferCheckEligibility hasNotAvailedLinkedOffer(Integer... offerIds) {
        if (eligible) {
            if (StringUtils.isBlank(context.getMsisdn())) {
                reason = OfferEligibilityStatusReason.MSISDN_REQUIRED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            Optional<Integer> availedOfferId = getAvailedOfferId(context.getUserPlanDetails(), offerIds);
            if (availedOfferId.isPresent()) {
                reason = OfferEligibilityStatusReason.AVAILED_LINKED_OFFER;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;

            }
        }
        return this;
    }

    public OfferCheckEligibility hasAppId(String... appIds) {
        if (eligible) {
            AppId currentAppId = context.getAppId();
            if (currentAppId == null || currentAppId == AppId.UNKNOWN) {
                reason = OfferEligibilityStatusReason.APP_ID_REQUIRED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            Set<AppId> availableAppIds = Arrays.stream(appIds).map(AppId::fromString).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(availableAppIds) && availableAppIds.contains(currentAppId)) {
                eligible = true;
                status = OfferEligibilityStatus.ELIGIBLE;
                return this;
            }
            reason = OfferEligibilityStatusReason.NOT_ELIGIBLE_FOR_APP_ID;
            status = OfferEligibilityStatus.NOT_ELIGIBLE;
            eligible = false;
        }
        return this;
    }

    public OfferCheckEligibility isUserInSegment(String... segmentIds) {
        if (eligible) {
            List<ThanksUserSegment> thanksUserSegment = context.getAllThanksSegments().get(context.getMsisdn());
            if (CollectionUtils.isEmpty(thanksUserSegment)) {
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                reason = OfferEligibilityStatusReason.THANKS_USER_SEGMENT_NOT_FOUND;
                eligible = false;
                return this;
            }
            Set<String> availableSegments = thanksUserSegment.stream().map(ThanksUserSegment::getServicePack).collect(Collectors.toSet());
            Set<String> segments = new HashSet<>(Arrays.asList(segmentIds));
            Optional<String> servicePack = segments.stream().filter(availableSegments::contains).findAny();
            if (servicePack.isPresent()) {
                eligible = true;
                status = OfferEligibilityStatus.ELIGIBLE;
            } else {
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                reason = OfferEligibilityStatusReason.THANKS_USER_SEGMENT_NOT_ELIGIBLE;
                eligible = false;
            }
        }
        return this;
    }

    public OfferCheckEligibility isUserNotInSegment(String... segmentIds) {
        if (eligible) {
            List<ThanksUserSegment> thanksUserSegment = context.getAllThanksSegments().values().stream().flatMap(Collection::parallelStream).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(thanksUserSegment)) {
                status = OfferEligibilityStatus.ELIGIBLE;
                reason = OfferEligibilityStatusReason.THANKS_USER_SEGMENT_NOT_FOUND;
                eligible = true;
                return this;
            }
            Set<String> availableSegments = thanksUserSegment.stream().map(ThanksUserSegment::getServicePack).collect(Collectors.toSet());
            Set<String> segments = new HashSet<>(Arrays.asList(segmentIds));
            Optional<String> servicePack = segments.stream().filter(availableSegments::contains).findAny();
            if (servicePack.isPresent()) {
                eligible = false;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                reason = OfferEligibilityStatusReason.THANKS_USER_SEGMENT_FOUND;
            } else {
                status = OfferEligibilityStatus.ELIGIBLE;
                reason = OfferEligibilityStatusReason.THANKS_USER_SEGMENT_NOT_FOUND;
                eligible = true;
            }
        }
        return this;
    }

    public OfferCheckEligibility hasCohortSegments(String... cohortSegments) {
        if (eligible) {
            List<ThanksUserSegment> currentCohortSegments = context.getAllThanksSegments().values().stream().filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(currentCohortSegments)) {
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                reason = OfferEligibilityStatusReason.THANKS_COHORT_NOT_FOUND;
                eligible = false;
                return this;
            }
            Set<String> currentCohort = currentCohortSegments.stream().map(ThanksUserSegment::getServicePack).collect(Collectors.toSet());
            Optional<String> foundCohort = Arrays.stream(cohortSegments).filter(currentCohort::contains).findAny();
            if (foundCohort.isPresent()) {
                eligible = true;
                status = OfferEligibilityStatus.ELIGIBLE;
            } else {
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                reason = OfferEligibilityStatusReason.THANKS_COHORT_NOT_ELIGIBLE;
                eligible = false;
            }
        }
        return this;
    }

    public OfferCheckEligibility isUserType(String... usertypes) {
        if (eligible) {
            if (StringUtils.isBlank(context.getMsisdn())) {
                reason = OfferEligibilityStatusReason.MSISDN_REQUIRED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            if (context.getMsisdnOperatorDetails() == null) {
                reason = OfferEligibilityStatusReason.INTERNAL_ERROR;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            Set<UserType> currentUserTypes = context.getMsisdnOperatorDetails().getAllUserTypes().keySet();
            Set<UserType> availableUserTypes = Arrays.stream(usertypes).map(UserType::fromValue).collect(Collectors.toSet());
            Optional<UserType> filteredUserType = currentUserTypes.stream().filter(availableUserTypes::contains).findAny();
            if (filteredUserType.isPresent()) {
                this.eligible = true;
                status = OfferEligibilityStatus.ELIGIBLE;
            } else {
                reason = OfferEligibilityStatusReason.OFFER_NOT_ELIGIBLE_FOR_USERTYPE;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
            }
            return this;
        }
        return this;
    }

    public OfferCheckEligibility isUserTypeNotIn(String... usertypes) {
        if (eligible) {
            if(isUserType(usertypes).eligible) {
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
            } else {
                this.eligible = true;
                status = OfferEligibilityStatus.ELIGIBLE;
                reason = OfferEligibilityStatusReason.OFFER_ELIGIBLE_FOR_NOT_IN_USER_TYPE;
            }
            return this;
        }
        return this;
    }

    public OfferCheckEligibility buildNumber(String os, Integer minBuildNum, Integer maxBuildNum) {
        if (eligible) {
            Os currentOS = context.getOs();
            if (currentOS == null) {
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                reason = OfferEligibilityStatusReason.OS_REQUIRED;
                eligible = false;
                return this;
            }
            Os availableOS = Os.getOsFromValue(os);
            if (!availableOS.equals(currentOS)) {
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                reason = OfferEligibilityStatusReason.OS_REQUIRED;
                eligible = false;
                return this;
            }
            Integer currentBuildNo = context.getBuildNo();
            if (minBuildNum > 0 && maxBuildNum > 0) {
                return isBuildNoInRange(minBuildNum, maxBuildNum, currentBuildNo);
            } else if (minBuildNum < 0) {
                return isBuildNoInRange(Integer.MIN_VALUE, maxBuildNum, currentBuildNo);
            } else if (maxBuildNum < 0) {
                return isBuildNoInRange(minBuildNum, Integer.MAX_VALUE, currentBuildNo);
            } else {
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                reason = OfferEligibilityStatusReason.NOT_ELIGIBLE_FOR_BUILD_NO;
                eligible = false;
                return this;
            }
        }
        return this;
    }

    private OfferCheckEligibility isBuildNoInRange(Integer minBuildNum, Integer maxBuildNum, Integer currentBuildNo) {
        if (currentBuildNo >= minBuildNum && currentBuildNo <= maxBuildNum) {
            this.eligible = true;
            status = OfferEligibilityStatus.ELIGIBLE;
        } else {
            status = OfferEligibilityStatus.NOT_ELIGIBLE;
            reason = OfferEligibilityStatusReason.NOT_ELIGIBLE_FOR_BUILD_NO;
            eligible = false;
        }
        return this;
    }

    public OfferCheckEligibility isAirtelUser() {
        if (eligible) {
            if (StringUtils.isBlank(context.getMsisdn())) {
                reason = OfferEligibilityStatusReason.MSISDN_REQUIRED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            if (context.getMsisdnOperatorDetails() == null) {
                reason = OfferEligibilityStatusReason.INTERNAL_ERROR;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            boolean isAirtelUser = checkAirtelUserEligibility(context);
            if (isAirtelUser) {
                this.eligible = true;
                status = OfferEligibilityStatus.ELIGIBLE;
            } else {
                reason = OfferEligibilityStatusReason.IS_NON_AIRTEL_USER;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
            }
            return this;
        }
        return this;
    }

    private boolean checkAirtelUserEligibility(OfferContext context) {
        MsisdnOperatorDetails msisdnOperatorDetails = context.getMsisdnOperatorDetails();
        if (msisdnOperatorDetails != null && msisdnOperatorDetails.getUserMobilityInfo() != null && msisdnOperatorDetails.getUserMobilityInfo().getCircle() != null) {
            return true;
        }
        List<ThanksUserSegment> thanksUserSegments = context.getAllThanksSegments().keySet().stream().map(context.getAllThanksSegments()::get).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(thanksUserSegments);
    }

    public OfferCheckEligibility isNonAirtelUser() {
        if (eligible) {
            if (StringUtils.isBlank(context.getMsisdn())) {
                reason = OfferEligibilityStatusReason.MSISDN_REQUIRED;
                eligible = false;
                return this;
            }
            if (context.getMsisdnOperatorDetails() == null) {
                reason = OfferEligibilityStatusReason.INTERNAL_ERROR;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            boolean isAirtelUser = checkAirtelUserEligibility(context);
            if (!isAirtelUser) {
                this.eligible = true;
                status = OfferEligibilityStatus.ELIGIBLE;
            } else {
                reason = OfferEligibilityStatusReason.IS_AIRTEL_USER;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
            }
            return this;
        }
        return this;
    }

    public static boolean isAirtelSriLanka(String msisdn) {
        if (StringUtils.isNotEmpty(msisdn)) {
            return (msisdn.startsWith("+9475") && msisdn.length() == 12) || (msisdn.startsWith("9475") && msisdn.length() == 11);
        }
        return false;
    }

    public OfferCheckEligibility isAirtelSriLankaUser() {
        if (eligible) {
            String msisdn = context.getMsisdn();
            if (StringUtils.isBlank(context.getMsisdn())) {
                reason = OfferEligibilityStatusReason.MSISDN_REQUIRED;
                eligible = false;
                return this;
            }
            if (isSriLankaNumber(msisdn)) {
                if (isAirtelSriLankaNumber(msisdn)) {
                    reason = OfferEligibilityStatusReason.IS_AIRTEL_SRILANKA_USER;
                    status = OfferEligibilityStatus.ELIGIBLE;
                    eligible = true;
                } else {
                    reason = OfferEligibilityStatusReason.IS_NON_AIRTEL_SRILANKA_USER;
                    status = OfferEligibilityStatus.NOT_ELIGIBLE;
                    eligible = false;
                }
            } else {
                reason = OfferEligibilityStatusReason.IS_NON_SRILANKA_USER;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
            }
            return this;
        }
        return this;
    }


    public OfferCheckEligibility belongsToGroup(String collectionName) {
        if (eligible) {
            String msisdn = context.getMsisdn();
            if (StringUtils.isBlank(msisdn)) {
                reason = OfferEligibilityStatusReason.MSISDN_REQUIRED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
            if (collectionName == null || StringUtils.isBlank(collectionName)) {
                // Invalid collection name?
                reason = OfferEligibilityStatusReason.COLLECTION_NAME_REQUIRED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }

            FilterUserDetails userDetails = filterDbDao.getFilterUserDetails(msisdn, collectionName);
            if (userDetails == null) {
                reason = OfferEligibilityStatusReason.USER_NOT_IN_COLLECTION_PROVIDED;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
                return this;
            }
        }
        return this;
    }

    public OfferCheckEligibility isNonAirtelSriLankaUser() {
        if (eligible) {
            String msisdn = context.getMsisdn();
            if (StringUtils.isBlank(context.getMsisdn())) {
                reason = OfferEligibilityStatusReason.MSISDN_REQUIRED;
                eligible = false;
                return this;
            }
            if (isSriLankaNumber(msisdn)) {
                if (!isAirtelSriLankaNumber(msisdn)) {
                    reason = OfferEligibilityStatusReason.IS_NON_AIRTEL_SRILANKA_USER;
                    status = OfferEligibilityStatus.ELIGIBLE;
                    eligible = true;
                } else {
                    reason = OfferEligibilityStatusReason.IS_AIRTEL_SRILANKA_USER;
                    status = OfferEligibilityStatus.NOT_ELIGIBLE;
                    eligible = false;
                }
            } else {
                reason = OfferEligibilityStatusReason.IS_NON_SRILANKA_USER;
                status = OfferEligibilityStatus.NOT_ELIGIBLE;
                eligible = false;
            }
            return this;
        }
        return this;
    }

    private boolean isAirtelSriLankaNumber(String msisdn) {
        if (StringUtils.isNotEmpty(msisdn)) {
            return (msisdn.startsWith("+9475") && msisdn.length() == 12) || (msisdn.startsWith("9475") && msisdn.length() == 11);
        }
        return false;
    }

    private boolean isSriLankaNumber(String msisdn) {
        if (StringUtils.isNotEmpty(msisdn)) {
            return (msisdn.startsWith("+94") && msisdn.length() == 12) || (msisdn.startsWith("94") && msisdn.length() == 11);
        }
        return false;
    }

    @Override
    public String toString() {
        return "OfferCheckEligibility{" +
                "status=" + status +
                ", offerId=" + offer.getId() +
                ", offerHierarchy=" + offer.getHierarchy() +
                ", offerProvisionType=" + offer.getProvisionType().name() +
                ", reason=" + reason +
                ", activePlanId=" + activePlanId +
                ", eligible=" + eligible +
                '}';
    }
}