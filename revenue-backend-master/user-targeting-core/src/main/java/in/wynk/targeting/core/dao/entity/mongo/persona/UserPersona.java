package in.wynk.targeting.core.dao.entity.mongo.persona;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class UserPersona implements Serializable {
    private static final long serialVersionUID = -2376104980665244920L;
    private PersonaDetails atv;
}
