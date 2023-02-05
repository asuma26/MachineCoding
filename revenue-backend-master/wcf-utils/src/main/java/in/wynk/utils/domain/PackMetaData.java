package in.wynk.utils.domain;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;

@Deprecated
public class PackMetaData {

    @Expose
    private final String purchaseImage;

    @Expose
    private final String descriptionImage;

    @Expose
    private final List<String> description;

    @Expose
    private final Map<String, String> uiData;

    @Expose
    private List<String> devices; // For external use: Consumed by ATV client

    @Expose
    private String packType; // For external use: Consumed by ATV client

    @Expose
    private String userType; // For external use: Consumed by ATV client

    @Expose
    private String segment; // For external use: Consumed by ATV client

    private List<String> platforms; // For internal use: Used to provision on hold packs

    public PackMetaData(String purchaseImage, String descriptionImage,
                        List<String> description, Map<String, String> uiData,
                        List<String> platforms, List<String> devices) {
        this.purchaseImage = purchaseImage;
        this.descriptionImage = descriptionImage;
        this.description = description;
        this.uiData = uiData;
        this.platforms = platforms;
        this.devices = devices;
    }

    public static PackMetaData newPackMetaData(String purchaseImage, String descriptionImage,
                                               List<String> description, Map<String, String> uiData, List<String> platforms,
                                               List<String> devices) {
        return new PackMetaData(purchaseImage, descriptionImage, description, uiData, platforms,
                devices);
    }

    private PackMetaData() {
        super();
        this.purchaseImage = null;
        this.descriptionImage = null;
        this.description = null;
        this.uiData = null;
        this.platforms = null;
        this.devices = null;
        this.segment = null;
        this.packType = null;
        this.userType = null;
    }

    public String getPurchaseImage() {
        return purchaseImage;
    }

    public String getDescriptionImage() {
        return descriptionImage;
    }

    public List<String> getDescription() {
        return description;
    }

    public Map<String, String> getUiData() {
        return uiData;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public String getPackType() {
        return packType;
    }

    public void setPackType(String packType) {
        this.packType = packType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }
}