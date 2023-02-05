package in.wynk.targeting.core.dao.entity.mongo.music;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class UserConfig implements Serializable {
    private static final long serialVersionUID = -2376104980665244920L;
    @SerializedName(value = "userAttributesList")
    private UserAttributes attributes;
}
