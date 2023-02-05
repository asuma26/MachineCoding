package in.wynk.order.core.constant;


import com.github.annotation.analytic.core.annotations.Analysed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum ExecutionType {

    SYNC("SYNC"), ASYNC("ASYNC");

    @Analysed(name = "executionType")
    private final String value;

    public String getValue() {
        return name();
    }

    public static ExecutionType fromString(String type) {
        if (StringUtils.isNotBlank(type)) {
            for (ExecutionType executionType : values()) {
                if (executionType.name().equalsIgnoreCase(type)) {
                    return executionType;
                }
            }
        }
        return SYNC;
    }

}
