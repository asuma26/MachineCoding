package in.wynk.coupon.core.service.impl;

import in.wynk.coupon.core.constant.*;
import in.wynk.coupon.core.dao.entity.Coupon;
import in.wynk.coupon.core.dao.entity.CouponCodeLink;
import in.wynk.coupon.core.dao.entity.CouponPair;
import in.wynk.coupon.core.dao.entity.UserCouponAvailedRecord;
import in.wynk.coupon.core.dao.repository.AvailedCouponsDao;
import in.wynk.coupon.core.dao.repository.WhitelistedCouponDao;
import in.wynk.coupon.core.dto.*;
import in.wynk.coupon.core.dto.CouponResponse.CouponResponseBuilder;
import in.wynk.coupon.core.expression.CouponEligibility;
import in.wynk.coupon.core.service.ICouponCacheService;
import in.wynk.coupon.core.service.ICouponCodeLinkService;
import in.wynk.coupon.core.service.ICouponManager;
import in.wynk.coupon.core.service.IUserProfileService;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.spel.IRuleEvaluator;
import in.wynk.spel.builder.DefaultStandardExpressionContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CouponManagerImpl implements ICouponManager {

    @Autowired
    private IRuleEvaluator ruleEvaluator;
    @Autowired
    private ICouponCodeLinkService couponCodeLinkService;
    @Autowired
    private ICouponCacheService couponCacheService;

    @Autowired
    private IUserProfileService userProfileService;
    @Autowired
    private AvailedCouponsDao availedCouponsDao;
    @Autowired
    private WhitelistedCouponDao whitelistedCouponDao;

    @Override
    public List<CouponDTO> getEligibleCoupons(CouponProvisionRequest request) {
        Collection<Coupon> coupons = couponCacheService.getAllCoupons();
        if (!CollectionUtils.isEmpty(coupons)) {
            CouponContext couponContext = CouponContext.builder()
                    .uid(request.getUid())
                    .msisdn(request.getMsisdn())
                    .itemId(request.getItemId())
                    .plan(request.getSelectedPlan())
                    .paymentCode(request.getPaymentCode())
                    .build();
            return coupons.stream().map(coupon -> {
                couponContext.setCoupon(coupon);
                couponContext.setCouponCodeLink(couponCodeLinkService.fetchCouponCodeLink(coupon.getId()));
                return couponContext;
            }).map(this::checkCouponEligibility)
                    .filter(couponResponse -> couponResponse.getState() == CouponProvisionState.ELIGIBLE)
                    .map(CouponResponse::getCoupon)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public CouponResponse evalCouponEligibility(CouponProvisionRequest request) {
        CouponContext couponContext = getCouponContext(request);
        return checkCouponEligibility(CouponConstant.COMMON_ELIGIBILITY_RULE_EXPRESSION, couponContext);
    }

    @Override
    public CouponResponse applyCoupon(CouponProvisionRequest request) {
        CouponContext couponContext = getCouponContext(request);
        CouponResponse couponResponse = checkCouponEligibility(couponContext);
        if (couponResponse.getState() != CouponProvisionState.INELIGIBLE) {
            UserCouponAvailedRecord record;
            Optional<UserCouponAvailedRecord> optional = availedCouponsDao.findById(request.getUid());
            CouponPair couponPair = CouponPair.builder().code(request.getCouponCode()).timestamp(System.currentTimeMillis()).provisionState(CouponProvisionState.APPLIED).build();
            if (optional.isPresent()) {
                record = optional.get();
                record.beforeCouponApplied();
                record.getCouponPairs().add(couponPair);
            } else {
                record = UserCouponAvailedRecord.builder().id(request.getUid()).couponPairs(Arrays.asList(couponPair)).build();
            }
            availedCouponsDao.save(record);
        }
        return couponResponse;
    }

    @Override
    public CouponResponse removeCoupon(String uid, String couponCode) {
        CouponResponseBuilder builder = CouponResponse.builder();
        Optional<UserCouponAvailedRecord> record = availedCouponsDao.findById(uid);
        if (record.isPresent() && !CollectionUtils.isEmpty(record.get().getCouponPairs())) {
            int pairIndex = -1;
                List<CouponPair> pairs = record.get().getCouponPairs();
            for (int i = 0; i < pairs.size(); i++) {
                if (pairs.get(i).getProvisionState() == CouponProvisionState.APPLIED && pairs.get(i).getCode().equalsIgnoreCase(couponCode)) {
                    pairIndex = i;
                    break;
                }
            }
            if (pairIndex >= 0) {
                pairs.remove(pairIndex);
                availedCouponsDao.save(record.get());
                builder.state(CouponProvisionState.REMOVED);
                return builder.build();
            }
        }
        throw new WynkRuntimeException(CouponErrorType.CP003);
    }

    @Override
    public void exhaustCoupon(String uid, String couponCode) {
        if (!StringUtils.isEmpty(couponCode)) {
            Optional<UserCouponAvailedRecord> optional = availedCouponsDao.findById(uid);
            if (optional.isPresent()) {
                CouponPair selectedPair = null;
                UserCouponAvailedRecord record = optional.get();
                for (CouponPair pair : record.getCouponPairs()) {
                    if (pair.getCode().equalsIgnoreCase(couponCode) && pair.getProvisionState() == CouponProvisionState.APPLIED) {
                        selectedPair = pair;
                        break;
                    }
                }
                if (selectedPair != null) {
                    selectedPair.setTimestamp(System.currentTimeMillis());
                    selectedPair.setProvisionState(CouponProvisionState.EXHAUSTED);
                    availedCouponsDao.save(record);
                    couponCodeLinkService.exhaustCouponCode(couponCode);
                    return;
                }
            }
        }
        throw new WynkRuntimeException(CouponErrorType.CP004);
    }

    private CouponContext getCouponContext(CouponProvisionRequest request) {
        CouponCodeLink couponLinkOption = couponCodeLinkService.fetchCouponCodeLink(request.getCouponCode());
        if (couponLinkOption != null) {
            Coupon coupon = couponCacheService.getCouponById(couponLinkOption.getCouponId());
            return CouponContext.builder()
                    .uid(request.getUid())
                    .msisdn(request.getMsisdn())
                    .service(request.getService())
                    .itemId(request.getItemId())
                    .paymentCode(request.getPaymentCode())
                    .coupon(coupon)
                    .plan(request.getSelectedPlan())
                    .couponCodeLink(couponLinkOption)
                    .build();
        }
        throw new WynkRuntimeException(CouponErrorType.CP002);
    }

    private CouponResponse checkCouponEligibility(CouponContext couponContext) {
        String couponRuleExpression = couponContext.getCoupon().getRuleExpression();
        String finalRuleExpression = StringUtils.isEmpty(couponRuleExpression) ?
                CouponConstant.COMMON_ELIGIBILITY_RULE_EXPRESSION :
                CouponConstant.COMMON_ELIGIBILITY_RULE_EXPRESSION + " && " + couponRuleExpression;
        return checkCouponEligibility(finalRuleExpression, couponContext);
    }

    private CouponResponse checkCouponEligibility(String ruleExpression, CouponContext couponContext) {
        Coupon coupon = couponContext.getCoupon();
        CouponResponseBuilder couponResponseBuilder = CouponResponse.builder().coupon(CouponDTO.from(coupon));
        CouponEligibility couponEligibility = CouponEligibility.builder().couponContext(couponContext).userProfileService(userProfileService).availedCouponsDao(availedCouponsDao).whitelistedCouponDao(whitelistedCouponDao).build();
        StandardEvaluationContext couponEvaluationContext = DefaultStandardExpressionContextBuilder.builder().rootObject(couponEligibility).build();
        try {
            if (ruleEvaluator.evaluate(ruleExpression, () -> couponEvaluationContext, Boolean.class)) {
                couponResponseBuilder.state(CouponProvisionState.ELIGIBLE);
            } else {
                couponResponseBuilder.coupon(CouponDTO.from(coupon)).error(couponEligibility.getCouponError()).state(CouponProvisionState.INELIGIBLE);
            }
        } catch (Exception e) {
            couponResponseBuilder.coupon(CouponDTO.from(coupon)).error(CouponError.builder().code(CouponEligibilityCode.ELIGIBILITY_FAILURE.name()).description("Unable to evaluate coupon eligibility for coupon code " + couponContext.getCouponCodeLink().getId()).build()).state(CouponProvisionState.ERROR);
        }
        CouponResponse couponResponse = couponResponseBuilder.build();

        if (Arrays.asList(CouponProvisionState.INELIGIBLE, CouponProvisionState.ERROR).contains(couponResponse.getState())) {
            log.warn(CouponLoggingMarker.COUPON_ELIGIBILITY_ERROR, "User not eligible for coupon code {} & coupon id {} , REASON -> code: {} & description: {}", couponContext.getCouponCodeLink().getId(), couponContext.getCouponCodeLink().getId(), couponResponse.getError().getCode(), couponResponse.getError().getDescription());
        }

        return couponResponseBuilder.build();
    }

}
