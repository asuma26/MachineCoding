package in.wynk.client.core.dao.entity;

import in.wynk.auth.dao.entity.Client;
import in.wynk.data.entity.MongoBaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@SuperBuilder
@Document(collection = "clients")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientDetails extends MongoBaseEntity implements Client {

    private String name;
    private String alias;
    private String secret;
    private String service;
    private String description;

    private List<String> authorities;
    private List<String> wynkServices;

    private Map<String, Object> meta;

    @Override
    public String getClientId() {
        return super.getId();
    }

    @Override
    public String getClientSecret() {
        return secret;
    }

    @Override
    public <T> Optional<T> getMeta(String key) {
        if (MapUtils.isNotEmpty(meta) && meta.containsKey(key))
            return Optional.of((T) meta.get(key));
        else
            return Optional.empty();
    }

}