package org.hhplus.ecommerce.common.config.caffeine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CaffeineCacheType {

    MEMBER_PROFILE("member", 12, 10000);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
