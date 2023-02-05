package in.wynk.cache.entity;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class LevelTwoCacheSpec extends CacheSpec {
    @Override
    public LevelTwoCacheSpec getSpec() {
        return this;
    }
}
