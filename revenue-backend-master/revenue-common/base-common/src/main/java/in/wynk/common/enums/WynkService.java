package in.wynk.common.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import com.github.annotation.analytic.core.annotations.AnalysedEntity;
import org.apache.commons.lang3.StringUtils;

import static in.wynk.common.constant.BaseConstants.SERVICE;

@AnalysedEntity
public enum WynkService {

    MUSIC("music"), AIRTEL_TV("airteltv"), BOOKS("books"), ARTIST_LIVE("artistLive"), UNKNOWN("unknown");
    @Analysed(name = SERVICE)
    private final String value;

    WynkService(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static WynkService fromString(String service) {
        if (StringUtils.isBlank(service)) {
            return UNKNOWN;
        }
        for (WynkService wynkService : values()) {
            if (wynkService.getValue().equalsIgnoreCase(service)) {
                return wynkService;
            }
        }
        return UNKNOWN;
    }
}
