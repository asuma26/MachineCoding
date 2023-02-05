package in.wynk.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.wynk.data.enums.State;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MongoBaseEntity {

    @Id
    private String id;
    @CreatedDate
    @JsonIgnore
    private Long createdAt;
    @Setter
    @JsonIgnore
    @LastModifiedDate
    private Long updatedAt;
    @JsonIgnore
    @CreatedBy
    private String createdBy;
    @Setter
    @JsonIgnore
    @LastModifiedBy
    private String updatedBy;
    @Setter
    @JsonIgnore
    @Builder.Default
    private State state = State.ACTIVE;

}
