package in.wynk.coupon.core.dto;

import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import in.wynk.coupon.core.dao.entity.Coupon;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AnalysedEntity
public class CouponDTO {

    private final String id;
    private final String title;
    private final String description;
    private final double discountPercent;

    public static CouponDTO from(Coupon coupon) {
        return CouponDTO.builder()
                .title(coupon.getTitle())
                .description(coupon.getDescription())
                .discountPercent(coupon.getDiscountPercent())
                .build();
    }

}
