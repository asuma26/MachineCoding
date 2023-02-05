package in.wynk.cache.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
public abstract class CacheSpec implements ICacheSpec {

   private final boolean nullable;
   private final long ttl;

}
