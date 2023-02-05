package in.wynk.targeting.core.dao.entity.mongo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

import static in.wynk.targeting.core.constant.AdConstants.DFP;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AdMeta implements Serializable {

    private static final long serialVersionUID = 8728521995904777433L;

    @Field("source")
    private final String source = DFP;

}
