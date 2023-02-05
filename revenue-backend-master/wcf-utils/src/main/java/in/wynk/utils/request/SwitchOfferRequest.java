package in.wynk.utils.request;

import java.util.List;

@Deprecated
public class SwitchOfferRequest {

    private List<Integer> offerIds;

    private boolean isSystemOffer;

    public boolean isSystemOffer() {
        return isSystemOffer;
    }

    public void setSystemOffer(boolean isSystemOffer) {
        this.isSystemOffer = isSystemOffer;
    }

    public List<Integer> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(List<Integer> offerIds) {
        this.offerIds = offerIds;
    }


}
