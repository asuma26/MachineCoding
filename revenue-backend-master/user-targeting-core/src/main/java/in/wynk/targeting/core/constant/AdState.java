package in.wynk.targeting.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AdState {

    ACTIVE_STATE("ACTIVE"), INACTIVE_STATE("INACTIVE");

    private final String state;

    @Override
    public String toString() {
        return state;
    }

}
