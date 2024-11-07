//package org.hhplus.ecommerce.common.cache.ehCache;
//
//import org.ehcache.CacheManager;
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.CacheManagerBuilder;
//import org.ehcache.config.builders.ExpiryPolicyBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.Duration;
//
//@Configuration
//public class EhCacheConfig {
//
//    @Bean
//    public CacheManager cacheManager() {
//        return CacheManagerBuilder.newCacheManagerBuilder()
//                .withCache("shortLivedCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(
//                                String.class, String.class, ResourcePoolsBuilder.heap(100))
//                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(5))))
//                .withCache("longLivedCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(
//                                String.class, String.class, ResourcePoolsBuilder.heap(100))
//                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofHours(1))))
//                .build(true);
//    }
//}
