package in.wynk.targeting.dto;

import in.wynk.auth.constant.AuthConstant;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Builder
@Getter
public class DeviceMeta {

    private final String id;
    private final String type;
    private final String os;
    private final String osType;
    private final String buildNumber;
    private final String appVersion;

    public static Optional<DeviceMeta> from(String did) {
        DeviceMeta deviceMeta = null;
        if (!StringUtils.isEmpty(did)) {
            String[] tokens = did.split(AuthConstant.PIPE_DELIMITER);
            deviceMeta = DeviceMeta.builder()
                                   .id(tokens[0])
                                   .type(tokens[1])
                                   .os(tokens[2])
                                   .osType(tokens[3])
                                   .buildNumber(tokens[4])
                                   .appVersion(tokens[5])
                                   .build();
            return Optional.of(deviceMeta);
        }
        return Optional.ofNullable(null);
    }

}
