package in.wynk.cache.entity;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class LevelOneCacheSpec extends CacheSpec {

    @Override
    public LevelOneCacheSpec getSpec() {
        return this;
    }
}
