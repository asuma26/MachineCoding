package in.wynk.coupon.core.expression;

import in.wynk.coupon.core.constant.CouponEligibilityCode;
import in.wynk.coupon.core.constant.CouponProvisionState;
import in.wynk.coupon.core.constant.ProvisionSource;
import in.wynk.coupon.core.dao.entity.CouponPair;
import in.wynk.coupon.core.dao.entity.UserCouponAvailedRecord;
import in.wynk.coupon.core.dao.entity.UserCouponWhiteListRecord;
import in.wynk.coupon.core.dao.repository.AvailedCouponsDao;
import in.wynk.coupon.core.dao.repository.WhitelistedCouponDao;
import in.wynk.coupon.core.dto.CouponContext;
import in.wynk.coupon.core.dto.CouponError;
import in.wynk.coupon.core.service.IUserProfileService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
public class CouponEligibility {

    private CouponError couponError;

    private final CouponContext couponContext;
    private final IUserProfileService userProfileService;
    private final AvailedCouponsDao availedCouponsDao;
    private final WhitelistedCouponDao whitelistedCouponDao;

    public boolean commonEligibility() {
        return isCouponExpired() && isCouponExhausted();
    }

    public boolean isSelectedPaymentCodeIn(String... paymentCodes) {
        boolean isEligible = false;

        String selectedPaymentCode = couponContext.getPaymentCode();
        if (!StringUtils.isEmpty(selectedPaymentCode)) {
            for (String paymentCode : paymentCodes) {
                if (selectedPaymentCode.equalsIgnoreCase(paymentCode)) {
                    isEligible = true;
                    break;
                }
            }
        }

        // skip payment based coupon eligibility, if source is not managed type
        if (couponContext.getSource() != ProvisionSource.MANAGED && !isEligible) {
            isEligible = true;
        }

        if (!isEligible) {
            couponError = this.buildCouponError(CouponEligibilityCode.INELIGIBLE_PAYMENT_METHOD.name(), "Coupon is not eligible for selected payment method " + selectedPaymentCode);
        }

        return isEligible;
    }

    public boolean hasActiveSubscriptionPlans(Integer... planIds) {
        boolean isEligible = false;
        List<Integer> activePlanIds = this.fetchActivePlanIds();
        for (Integer planId : planIds) {
            if (activePlanIds.contains(planId)) {
                isEligible = true;
                break;
            }
        }

        if (!isEligible) {
            couponError = this.buildCouponError(CouponEligibilityCode.INELIGIBLE_ACTIVE_PLAN_ID.name(), "Coupon not applicable for user's active plan ids " + activePlanIds);
        }

        return isEligible;
    }

    public boolean hasCohortSegments(String... cohortSegments) {
        boolean isEligible = false;

        Set<String> currentCohortSegments = fetchThanksSegment().values().stream().filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toSet());
        Optional<String> foundCohort = Arrays.stream(cohortSegments).filter(currentCohortSegments::contains).findAny();

        if (foundCohort.isPresent()) {
            isEligible = true;
        }

        if (!isEligible) {
            couponError = this.buildCouponError(CouponEligibilityCode.INELIGIBLE_COHORT_SEGMENT.name(), "Coupon not applicable for user's active cohorts segments");
        }

        return isEligible;
    }

    public boolean isUserInSegment(String... segmentIds) {
        boolean isEligible = false;
        Optional<String> foundSegment = Optional.empty();

        if (fetchThanksSegment().size() > 0) {
            Set<String> activeSegments = fetchThanksSegment().get(couponContext.getMsisdn()).stream().collect(Collectors.toSet());
            foundSegment = Arrays.stream(segmentIds).filter(activeSegments::contains).findAny();
        }


        if (foundSegment.isPresent()) {
            isEligible = true;
        }

        if (!isEligible) {
            couponError = this.buildCouponError(CouponEligibilityCode.INELIGIBLE_THANKS_SEGMENT.name(), "Coupon not applicable for user's active thanks segment");
        }

        return isEligible;
    }

    public boolean isUserAgeOnApp(int days) {
        return false;
    }

    public boolean isUserWhitelisted() {
        boolean isEligible = false;

        List<UserCouponWhiteListRecord> records = fetchWhitelistedRecords();
        if (!CollectionUtils.isEmpty(records)) {
            for (UserCouponWhiteListRecord record : records) {
                if (record.getCouponId().equals(couponContext.getCoupon().getId())) {
                    isEligible = true;
                    break;
                }
            }
        }

        if (!isEligible) {
            couponError = this.buildCouponError(CouponEligibilityCode.INELIGIBLE_WHITELISTED_USER.name(), "whitelisted user is not eligible for this coupon " + couponContext.getCoupon().getId());
        }

        return isEligible;
    }

    public boolean isUserActiveOnApp() {
        return false;
    }

    public boolean isSongPlayBackCountGreaterThanOrEquals(long playBackCount) {
        return false;
    }

    public boolean isUserPersonaEligible() {
        return false;
    }

    public boolean isPlanToBeSubscribedIn(int... planIds) {
        boolean isEligible = false;
        if (couponContext.getPlan() != null && Arrays.asList(planIds).contains(couponContext.getPlan().getId())) {
            isEligible = true;
        }

        if (!isEligible) {
            if (couponContext.getPlan() != null) {
                couponError = this.buildCouponError(CouponEligibilityCode.INELIGIBLE_PURCHASING_PLAN_ID.name(), "Coupon not applicable for plan " + couponContext.getPlan().getId() + " to be subscribed");
            } else {
                couponError = this.buildCouponError(CouponEligibilityCode.INELIGIBLE_PURCHASING_ITEM_ID.name(), "Coupon not applicable for item id" + couponContext.getItemId());
            }
        }

        return isEligible;
    }

    public boolean isItemToBePurchasedIn(String... itemIds) {
        boolean isEligible = false;
        if (!StringUtils.isEmpty(couponContext.getItemId()) && Arrays.asList(itemIds).contains(couponContext.getItemId())) {
            isEligible = true;
        }

        if (!isEligible) {
            couponError = this.buildCouponError(CouponEligibilityCode.INELIGIBLE_PURCHASING_ITEM_ID.name(), "Coupon not applicable for item id " + couponContext.getItemId() + " to be purchased");
        }

        return isEligible;
    }

    public boolean limitCouponRedemption(long count) {
        boolean isEligible = false;
        long availedCount = 0;
        UserCouponAvailedRecord record = fetchAvailedCoupon();
        List<CouponPair> couponPairs = record.getCouponPairs();
        if (!CollectionUtils.isEmpty(couponPairs)) {
            couponPairs = couponPairs.stream().filter(couponPair -> couponPair.getProvisionState() == CouponProvisionState.EXHAUSTED).collect(Collectors.toList());
            for (CouponPair couponPair : couponPairs) {
                if (couponPair.getCode().equals(couponContext.getCouponCodeLink().getId())) {
                    availedCount++;
                }
            }
        }

        if (availedCount < count) {
            isEligible = true;
        }

        if (!isEligible) {
            couponError = this.buildCouponError(CouponEligibilityCode.COUPON_LIMIT_EXCEED.name(), "Coupon " + couponContext.getCouponCodeLink().getId() + " redemption limit exceed");
        }

        return isEligible;
    }

    /**
     * Internal common eligibility methods
     **/

    private boolean isCouponExpired() {
        boolean isEligible = false;
        if (couponContext.getCoupon().getExpiry() > System.currentTimeMillis()) {
            isEligible = true;
        }

        if (!isEligible) {
            couponError = this.buildCouponError(CouponEligibilityCode.COUPON_EXPIRED.name(), "coupon " + couponContext.getCoupon().getId() + " is expired");
        }

        return isEligible;
    }

    private boolean isCouponExhausted() {
        boolean isEligible = false;

        final long totalCouponCount = couponContext.getCouponCodeLink().getTotalCount();
        final long exhaustedCouponCount = couponContext.getCouponCodeLink().getExhaustedCount();

        if (totalCouponCount - exhaustedCouponCount > 0) {
            isEligible = true;
        }

        if (!isEligible) {
            couponError = this.buildCouponError(CouponEligibilityCode.COUPON_LIMIT_EXCEED.name(), "coupon " + couponContext.getCoupon().getId() + " limit is exceeded");
        }

        return isEligible;
    }

    /**
     * Internal data aggregation methods
     **/

    private UserCouponAvailedRecord fetchAvailedCoupon() {
        if (couponContext.getCouponAvailedRecord() == null) {
            Optional<UserCouponAvailedRecord> optional = this.availedCouponsDao.findById(couponContext.getUid());
            if (optional.isPresent()) {
                couponContext.setCouponAvailedRecord(optional.get());
            } else {
                couponContext.setCouponAvailedRecord(UserCouponAvailedRecord.builder().id(couponContext.getUid()).couponPairs(Collections.EMPTY_LIST).build());
            }
        }
        return couponContext.getCouponAvailedRecord();
    }

    private List<UserCouponWhiteListRecord> fetchWhitelistedRecords() {
        if (couponContext.getCouponWhiteListRecords() == null) {
            List<UserCouponWhiteListRecord> whiteListRecords = whitelistedCouponDao.findByUid(couponContext.getUid());
            if (whiteListRecords != null) {
                couponContext.setCouponWhiteListRecords(whiteListRecords);
            } else {
                couponContext.setCouponWhiteListRecords(Collections.EMPTY_LIST);
            }
        }
        return couponContext.getCouponWhiteListRecords();
    }

    private List<Integer> fetchActivePlanIds() {
        if (couponContext.getUserProfile().getActivePlanId() != null) {
            return couponContext.getUserProfile().getActivePlanId();
        }
        List<Integer> planIds = userProfileService.fetchActivePlans(couponContext.getUid(), couponContext.getPlan().getService());
        couponContext.getUserProfile().setActivePlanId(planIds);
        return couponContext.getUserProfile().getActivePlanId();
    }

    private Map<String, List<String>> fetchThanksSegment() {
        if (couponContext.getUserProfile().getSegments() != null) {
            return couponContext.getUserProfile().getSegments();
        }
        couponContext.getUserProfile().setSegments(userProfileService.fetchThanksSegment(couponContext.getMsisdn(), couponContext.getPlan().getService()));
        return couponContext.getUserProfile().getSegments();
    }

    private CouponError buildCouponError(String code, String description) {
        return CouponError.builder().code(code).description(description).build();
    }

}
