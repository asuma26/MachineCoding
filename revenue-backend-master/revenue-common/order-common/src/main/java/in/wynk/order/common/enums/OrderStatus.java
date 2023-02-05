package in.wynk.order.common.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AnalysedEntity
@RequiredArgsConstructor
public enum OrderStatus {

    ACKNOWLEDGED("ACKNOWLEDGED"), DEFERRED("DEFERRED"), FULFILLED("FULFILLED"), FAILED("FAILED"), UNKNOWN("UNKNOWN");

    @Analysed
    private final String status;

}
