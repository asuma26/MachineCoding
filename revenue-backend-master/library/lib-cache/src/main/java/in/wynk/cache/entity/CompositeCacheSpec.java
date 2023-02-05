package in.wynk.cache.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CompositeCacheSpec implements ICacheSpec {

    private final CacheSpec l1CacheSpec;
    private final CacheSpec l2CacheSpec;


    @Override
    public CompositeCacheSpec getSpec() {
        return this;
    }

}
