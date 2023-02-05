package in.wynk.common.enums;

import com.github.annotation.analytic.core.annotations.Analysed;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DeviceType {

    PHONE("PHONE"),
    TABLET("TABLET");
    @Analysed
    private String value;
}
