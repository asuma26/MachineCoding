package in.wynk.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyResponse extends BaseResponse<Void> {
    @Override
    public Void getData() {
        return null;
    }

    public static EmptyResponse response() {
        return new EmptyResponse();
    }

}
