package in.wynk.utils.domain;

import com.google.gson.annotations.Expose;
import in.wynk.utils.constant.PriceUnit;

@Deprecated
public class Amount implements Comparable<Amount> {

    @Expose
    private int price;

    @Expose
    private final PriceUnit unit;

    public static Amount ZERO = Amount.newAmountFor(0, PriceUnit.RUPEE);

    private Amount(int price, PriceUnit unit) {
        super();
        this.price = price;
        this.unit = unit;
    }

    public static Amount newAmountFor(int price, PriceUnit unit) {
        return new Amount(price, unit);
    }

    public int getRoundedPriceInRupees() {
        if (PriceUnit.RUPEE.equals(unit)) {
            return price;
        } else if (PriceUnit.PAISE.equals(unit)) {
            return price / 100;
        } else if (PriceUnit.LKR.equals(unit)) {
            return price;
        }
        return 0;
    }

    public int getPriceInPaise() {
        if (PriceUnit.RUPEE.equals(unit)) {
            return price * 100;
        } else if (PriceUnit.PAISE.equals(unit)) {
            return price;
        }
        return 0;
    }

    public PriceUnit getUnit() {
        return unit;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int compareTo(Amount amount) {
        if (this.getPriceInPaise() > amount.getPriceInPaise()) {
            return -1;
        } else if (this.getPriceInPaise() == amount.getPriceInPaise()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "Amount [price=" + price + ", unit=" + unit + "]";
    }

    private Amount() {
        price = 0;
        unit = null;
    }
}
