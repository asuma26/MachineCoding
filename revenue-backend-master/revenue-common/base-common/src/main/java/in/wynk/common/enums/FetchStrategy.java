package in.wynk.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FetchStrategy {

    DIRECT_SOURCE_EXTERNAL_CACHED("direct_source_external_cached"),
    DIRECT_SOURCE_INTERNAL_CACHED("direct_source_internal_cached"),
    DIRECT_SOURCE_EXTERNAL_WITHOUT_CACHE("direct_source_external_without_cache"),
    DIRECT_SOURCE_INTERNAL_WITHOUT_CACHE("direct_source_internal_without_cache"),
    CACHE_WITH_ASYNC_UPDATE("cache_with_async_update"),
    ASYNC_DIRECT_SOURCE_EXTERNAL("async_direct_source_external"),
    ASYNC_DIRECT_SOURCE_INTERNAL("async_direct_source_internal"),
    LATEST_FROM_CACHE("latest_from_cache"),
    CACHE("cache");

    private String type;

}
