package in.wynk.utils.dto;

import com.google.gson.annotations.Expose;
import in.wynk.utils.constant.PackType;
import in.wynk.utils.constant.PaymentMethod;
import in.wynk.utils.domain.SubscriptionPack;

@Deprecated
public class PackPeriodicElement {

    @Expose
    private int partnerProductId;
    @Expose
    private int productId;
    @Expose
    private String title;
    @Expose
    private String cpName;
    @Expose
    private String packageGroup;
    @Expose
    private PackType packageType;
    @Expose
    private double price;
    @Expose
    private int validity;
    @Expose
    private String paymentMethod;
    @Expose
    private int hierarchy;
    @Expose
    private boolean isDeprecated;

    public void setPartnerProductId(int partnerProductId) {
        this.partnerProductId = partnerProductId;
    }


    public void setProductId(int productId) {
        this.productId = productId;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void setCpName(String cpName) {
        this.cpName = cpName;
    }


    public void setPackageGroup(String packageGroup) {
        this.packageGroup = packageGroup;
    }


    public void setPackageType(PackType packageType) {
        this.packageType = packageType;
    }


    public void setPrice(double price) {
        this.price = price;
    }


    public void setValidity(int validity) {
        this.validity = validity;
    }


    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod.getValue();
    }


    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }


    public int getPartnerProductId() {
        return partnerProductId;
    }


    public int getProductId() {
        return productId;
    }


    public String getTitle() {
        return title;
    }


    public String getCpName() {
        return cpName;
    }


    public String getPackageGroup() {
        return packageGroup;
    }


    public PackType getPackageType() {
        return packageType;
    }


    public double getPrice() {
        return price;
    }


    public int getValidity() {
        return validity;
    }


    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.fromValue(paymentMethod);
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public boolean isDeprecated() {
        return isDeprecated;
    }

    public void setDeprecated(boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }

    public PackPeriodicElement from(SubscriptionPack pack) {
        this.partnerProductId = pack.getPartnerProductId();
        this.productId = pack.getProductId();
        this.title = pack.getTitle();
        this.cpName = pack.getCpName();
        this.packageGroup = pack.getPackageGroup();
        this.packageType = pack.getPackageType();
        this.price = pack.getPricing().getRoundedPriceInRupees();
        this.validity = pack.getPackPeriod().getValidityInDays();
        this.paymentMethod = pack.getPaymentMethod().getValue();
        this.hierarchy = pack.getHierarchy();
        this.isDeprecated = pack.isDeprecated();
        return this;
    }

}
