package in.wynk.subscription.core.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.wynk.data.enums.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMongoSubscriptionBaseEntity {

    @Id
    private int id;
    @CreatedDate
    @JsonIgnore
    private Long createdAt;
    @Setter
    @JsonIgnore
    @LastModifiedDate
    private Long updatedAt;
    @CreatedBy
    private String createdBy;
    @Setter
    @JsonIgnore
    @LastModifiedBy
    private String updatedBy;
    @Setter
    @JsonIgnore
    private State state;
}
