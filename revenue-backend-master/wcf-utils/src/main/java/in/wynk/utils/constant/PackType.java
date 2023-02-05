package in.wynk.utils.constant;

@Deprecated
public enum PackType {
    ONE_TIME_SUBSCRIPTION("OneTimeSubscription"), SUBSCRIPTION("Subscription");

    private final String value;

    PackType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PackType getPackTypeFromName(String name) {
        for(PackType packType : values()) {
            if(packType.getValue().equals(name)) {
                return packType;
            }
        }
        return null;
    }
}