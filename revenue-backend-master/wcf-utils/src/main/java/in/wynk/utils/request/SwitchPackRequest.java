package in.wynk.utils.request;

import java.util.List;

@Deprecated
public class SwitchPackRequest {

    private List<Integer> packIds;

    private boolean isDeprecated;

    public List<Integer> getPackIds() {
        return packIds;
    }

    public void setPackIds(List<Integer> packIds) {
        this.packIds = packIds;
    }

    public boolean isDeprecated() {
        return isDeprecated;
    }

    public void setDeprecated(boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }

}
