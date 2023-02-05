package in.wynk.utils.service.coupons.impl;

import in.wynk.coupon.core.dao.entity.CouponCodeLink;
import in.wynk.coupon.core.dao.repository.CouponCodeLinkDao;
import in.wynk.data.enums.State;
import in.wynk.exception.WynkRuntimeException;
import in.wynk.utils.constant.WcfUtilsErrorType;
import in.wynk.utils.service.coupons.ICouponsCodeLinkService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponsCodeLinkServiceImpl implements ICouponsCodeLinkService {

    private final CouponCodeLinkDao couponCodeLinkDao;

    public CouponsCodeLinkServiceImpl(CouponCodeLinkDao couponCodeLinkDao) {
        this.couponCodeLinkDao = couponCodeLinkDao;
    }

    @Override
    public List<CouponCodeLink> generate(Integer n, String couponId, String totalCount) {
        List<CouponCodeLink> couponCodeLinks = new ArrayList<>();
        while (couponCodeLinks.size() < n) {
            String tempCoupon = RandomStringUtils.random(6, true, true);
            CouponCodeLink codeLink = CouponCodeLink.builder()
                    .id(tempCoupon)
                    .state(State.ACTIVE)
                    .couponId(couponId)
                    .totalCount(Long.parseLong(totalCount))
                    .build();
            try {
                couponCodeLinkDao.insert(codeLink);
                couponCodeLinks.add(codeLink);
            } catch (Exception e) {
                continue;
            }
        }
        return couponCodeLinks;
    }

    @Override
    public CouponCodeLink save(CouponCodeLink couponCodeLink) {
        return couponCodeLinkDao.save(couponCodeLink);
    }

    @Override
    public CouponCodeLink update(CouponCodeLink couponCodeLink) {
        CouponCodeLink codeLink = find(couponCodeLink.getId());
        return save(couponCodeLink);
    }

    @Override
    public void switchState(String id, State state) {
        CouponCodeLink codeLink = find(id);
        codeLink.setState(state);
        save(codeLink);
    }

    @Override
    public CouponCodeLink find(String id) {
        return couponCodeLinkDao.findById(id).orElseThrow(() -> new WynkRuntimeException(WcfUtilsErrorType.WCF008));
    }

    @Override
    public List<CouponCodeLink> findAll(Pageable pageable) {
        return couponCodeLinkDao.findAll(pageable).getContent();
    }

}
