package in.wynk.ut.base.batch.mapper;

import in.wynk.ut.base.model.UserTarget;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class UserTargetFieldSetMapper implements FieldSetMapper<UserTarget> {

    private String adid;
    private Boolean targeted;

    private UserTargetFieldSetMapper(String adid, Boolean targeted) {
        this.adid = adid;
        this.targeted = targeted;
    }

    @Override
    public UserTarget mapFieldSet(FieldSet fieldSet) throws BindException {
        return new UserTarget
                            .UserTargetBuilder()
                            .uid(fieldSet.readString(0))
                            .adid(adid)
                            .targeted(targeted)
                            .build();
    }

    public static FieldSetMapper<UserTarget> instantiate(String adid, Boolean targeted) {
        return new UserTargetFieldSetMapper(adid, targeted);
    }

}
