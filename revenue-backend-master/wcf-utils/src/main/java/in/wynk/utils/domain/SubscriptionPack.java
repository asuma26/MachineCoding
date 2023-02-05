package in.wynk.utils.domain;

import com.google.gson.annotations.Expose;
import in.wynk.utils.constant.PackState;
import in.wynk.utils.constant.PackType;
import in.wynk.utils.constant.PaymentMethod;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Deprecated
@Document(collection = "SubscriptionPack")
public class SubscriptionPack {

    @Id
    @Expose
    private int productId;
    @Expose
    private final String _class = "com.bsb.portal.se.dto.SubscriptionPack";
    @Expose
    private String extProductId;

    @Expose
    private int partnerProductId;

    @Expose
    private String service;

    @Expose
    private Amount pricing;

    @Expose
    private String paymentMethod;

    @Expose
    private PackPeriod packPeriod;

    @Expose
    private String packageGroup;

    @Expose
    private String packageType;
    @Expose
    private int hierarchy;

    @Expose
    private String title;

    @Expose
    private String cpName;

    private String mediaType;

    private ChargingCredentials chargingCredentials;

    private PackMessages messages;

    @Expose
    private PackMetaData packMetaData;

    private long createTimestamp;

    @Expose
    private Integer paidProductId;

    private Discount discount;

    private String eligiblity;

    private String renewalCheckAt = "NONE";

    @Expose
    private boolean isDeprecated;

    private List<Integer> childPacks;

    private PackState packState;

    public PackState getPackState() {
        return packState;
    }

    public void setPackState(PackState packState) {
        this.packState = packState;
    }

    public int getProductId() {
        return productId;
    }

    public String getExtProductId() {
        return extProductId;
    }

    public int getPartnerProductId() {
        return partnerProductId;
    }

    public Amount getPricing() {
        return pricing;
    }

    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.fromValue(paymentMethod);
    }

    public PackPeriod getPackPeriod() {
        return packPeriod;
    }

    public String getPackageGroup() {
        return packageGroup;
    }

    public PackType getPackageType() {
        return PackType.getPackTypeFromName(packageType);
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public String getTitle() {
        return title;
    }

    public String getCpName() {
        return cpName;
    }

    public ChargingCredentials getChargingCredentials() {
        return chargingCredentials;
    }

    public PackMessages getMessages() {
        return (messages == null ? new PackMessages() : messages);
    }

    public PackMetaData getPackMetaData() {
        return packMetaData;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public String getMediaType() {
        return mediaType;
    }

    public Integer getPaidProductId() {
        return paidProductId;
    }

    public Discount getDiscount() {
        return discount;
    }

    public String getEligiblity() {
        return eligiblity;
    }

    public boolean getIsDeprecated() {
        return isDeprecated;
    }

    public String getService() {
        return service;
    }

    public void setIsDeprecated(boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setExtProductId(String extProductId) {
        this.extProductId = extProductId;
    }

    public void setPartnerProductId(int partnerProductId) {
        this.partnerProductId = partnerProductId;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setPricing(Amount pricing) {
        this.pricing = pricing;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPackPeriod(PackPeriod packPeriod) {
        this.packPeriod = packPeriod;
    }

    public void setPackageGroup(String packageGroup) {
        this.packageGroup = packageGroup;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setChargingCredentials(ChargingCredentials chargingCredentials) {
        this.chargingCredentials = chargingCredentials;
    }

    public void setMessages(PackMessages messages) {
        this.messages = messages;
    }

    public void setPackMetaData(PackMetaData packMetaData) {
        this.packMetaData = packMetaData;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public void setPaidProductId(Integer paidProductId) {
        this.paidProductId = paidProductId;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public void setEligiblity(String eligiblity) {
        this.eligiblity = eligiblity;
    }

    public String getRenewalCheckAt() {
        return renewalCheckAt;
    }

    public void setRenewalCheckAt(String renewalCheckAt) {
        this.renewalCheckAt = renewalCheckAt;
    }

    public boolean isDeprecated() {
        return isDeprecated;
    }

    public void setDeprecated(boolean deprecated) {
        isDeprecated = deprecated;
    }

    public List<Integer> getChildPacks() {
        return childPacks;
    }

    public void setChildPacks(List<Integer> childPacks) {
        this.childPacks = childPacks;
    }

    @Override
    public String toString() {
        return "SubscriptionPack{" +
                "productId=" + productId +
                ", extProductId='" + extProductId + '\'' +
                ", partnerProductId=" + partnerProductId +
                ", service='" + service + '\'' +
                ", pricing=" + pricing +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", packPeriod=" + packPeriod +
                ", packageGroup='" + packageGroup + '\'' +
                ", packageType='" + packageType + '\'' +
                ", hierarchy=" + hierarchy +
                ", title='" + title + '\'' +
                ", cpName='" + cpName + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", chargingCredentials=" + chargingCredentials +
                ", messages=" + messages +
                ", packMetaData=" + packMetaData +
                ", createTimestamp=" + createTimestamp +
                ", paidProductId=" + paidProductId +
                ", discount=" + discount +
                ", eligiblity='" + eligiblity + '\'' +
                ", renewalCheckAt='" + renewalCheckAt + '\'' +
                ", isDeprecated=" + isDeprecated +
                ", childPacks=" + childPacks +
                ", packState=" + packState +
                '}';
    }
}