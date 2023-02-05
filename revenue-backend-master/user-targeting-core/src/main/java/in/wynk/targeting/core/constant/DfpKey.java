package in.wynk.targeting.core.constant;

public enum DfpKey {

    CURRENT_SEGMENT("currSeg"), PREVIOUS_SEGMENT("prevSeg"), USER_TYPE("userType"), GENRES("1_genre"),
    LANGUAGES("1_language"), SELECTED_LANGUAGE("sel_language"), CIRCLE("circle");

    private final String key;

    DfpKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

}
