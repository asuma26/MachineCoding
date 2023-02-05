package in.wynk.subscription.dto;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * @author Abhishek
 * @created 29/06/20
 */
@AnalysedEntity
@Getter
@Builder
public class Userdata {
    @Analysed
    private Map<String, List<String>> thanksSegment;
}