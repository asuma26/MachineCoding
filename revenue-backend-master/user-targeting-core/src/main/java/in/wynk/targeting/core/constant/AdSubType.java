package in.wynk.targeting.core.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AdSubType {

    MAST_HEAD("MAST_HEAD"), PRE_ROLL("PRE_ROLL"), MID_ROLL("MID_ROLL");

    private final String subtype;

    @Override
    public String toString() {
        return subtype;
    }
}
