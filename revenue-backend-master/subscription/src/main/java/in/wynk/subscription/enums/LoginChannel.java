package in.wynk.subscription.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Abhishek
 * @created 01/07/20
 */
@AllArgsConstructor
@Getter
@AnalysedEntity
public enum LoginChannel {
    HEADER_ENRICHMENT("HEADER_ENRICHMENT"), IMSI("IMSI"), DTH("DTH");

    @Analysed
    private String channel;
}
