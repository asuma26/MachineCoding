package in.wynk.utils.domain;

@Deprecated
public class Discount {

    private int    offerId;
    private Amount discount;

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public Amount getDiscount() {
        return discount;
    }

    public void setDiscount(Amount discount) {
        this.discount = discount;
    }

}