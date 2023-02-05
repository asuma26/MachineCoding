package in.wynk.targeting.core.dao.entity.mongo.persona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceDetails implements Serializable {
    private static final long serialVersionUID = 4079494594300141102L;
    private String fcm;
    private String name;
    private String type;
    private String deviceId;
    private String updatedAt;
}
