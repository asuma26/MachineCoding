package in.wynk.subscription.core.dao.entity;

import in.wynk.data.entity.MongoBaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@SuperBuilder
@Document(collection = "partners")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Partner extends MongoBaseEntity {

    private String logo;
    private String icon;
    private String name;
    private String portrait;
    private Map<String, List<String>> contentImages;
    private String packGroup;
    private String description;
    private String colorCode;
    private String service;

}
