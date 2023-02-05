package in.wynk.targeting.dto;

import java.util.List;

/**
 * @author Abhishek
 * @created 06/05/20
 */
public class UserTargetingSegmentsRequest {
    private String uid;
    private List<String> clients;
    private List<String> userIds;
    private String deviceId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
