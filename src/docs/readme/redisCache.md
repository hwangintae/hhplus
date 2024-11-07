조회가 오래 걸리게 되면 안좋은 사용자 경험을 주게 되고,
slow query가 빈번하게 조회를 해야한다면 DB에 부담이 된다.
결국 전체 서비스의 성능 저하의 결과를 가져올 수 있다.

조회 시 결과에 변화가 적은 데이터라면 굳이 계속 DB를 조회할 필요가 있을까?

이러한 경우 캐시를 통해 문제를 해결할 수 있다.

캐시의 종류는 크게 **로컬 캐시**와 **글로벌 캐시**가 있는데
로컬 캐시는 spring boot 내장 캐시, 글로벌 캐시는 redis 캐시가 있다.

정보 요청 시, 캐시에 있으면 캐시에 있는 정보를 반환하고, 없으면 RDB를 조회한 결과를 반환하고 캐시에 등록한다.

따라서, 간단하게 생각하면 로컬 캐시는 네트워크 I/O가 발생하지 않아 글로벌 캐시보다 빠른 결과를 가져올 수 있다.

그러나 다음과 같은 상황에서는 경우 결과는 다를 수 있다.

- 최근 5일간 많이 판매된 상위 상품 5개를 조회한다.
- TTL은 24시간 이고, 매일 00시에 캐시를 지운다.
- RDB에서 상위 상품 조회는 5초 이상 걸린다.
- 분산환경으로 10개의 인스턴스가 있다.

이럴 경우 글로벌 캐시는 최초 1명만 5초 이상의 시간이 걸릴 것이고, 내장 캐시일 경우 최대 10명이 5초 이상의 시간이 걸릴 것이다.

따라서, 분산환경에서는 글로벌 캐시가 합리적인 선택이 될 수 있다.

---
## 성능 테스트 결과 (기준일 2024-11-07)
- k6를 이용한 **최근 5일간 많이 판매된 상위 5개 상품**을 구하는 성능 테스트를 진행했습니다.
- 테스트 데이터는 다음과 같습니다.
- 데이터
- ![데이터](/src/docs/images/dayAndCnt.png)
- 총합(10,000,000)
- ![총합](/src/docs/images/totalCnt.png)
- query 응답 결과 (4s 32ms)
- ![query](/src/docs/images/query.png)
- 100명이 10초간 테스트 (테스트 자체가 안됨..)
- ![100and10](/src/docs/images/noCache100and10.png)
- 100명이 200초간 테스트 (전부 실패)
- ![100and200](/src/docs/images/noCache100and200.png)
- 캐시 적용 후 100명이 10초간 테스트(평균 응답 시간 24.83ms)
- ![cache100and10](/src/docs/images/cache100and10.png)

- 캐시 적용(TTL 하루)
    ```
    @Cacheable(value = "popularItemsCache", key = "#from + '_' + #limit", cacheManager = "redisCacheManager")
    public List<PopularItemsResult> getPopularItems(int from, int limit) {
        return orderItemRepository.findPopularItems(from, limit);
    }
    
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        ...생략...
        Map<String, RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();
        cacheConfigurationMap.put("popularItemsCache", defaultConfig.entryTtl(Duration.ofDays(1L)));
        ...생략...
    }
    ```
- 캐시 적용 결과
- ![redisKeyAndTTL](/src/docs/images/redisKeyAndTTL.png)

- 캐시 삭제
  ```
  @Scheduled(cron = "0 0 0 * * *")
  @CacheEvict(value = "popularItemsCache", allEntries = true)
  public void clearPopularItemsCache() {
      log.info("clear Popular Items Cache");
  }
  ```

- 캐시 삭제 결과
- ![clearRedisCache](/src/docs/images/clearRedisCache.png)
---
## etc
캐시를 사용하기 위해서는 CacheManager를 등록해줘야 하는데 다음과 같다.


### EhCache
```
@Configuration
public class EhCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("shortLivedCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class, String.class, ResourcePoolsBuilder.heap(100))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(5))))
                .withCache("longLivedCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class, String.class, ResourcePoolsBuilder.heap(100))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofHours(1))))
                .build(true);
    }
}
```

### Caffeine
```
@Configuration
public class CaffeineConfig {

    @Bean
    public List<CaffeineCache> caffeineCaches() {
        return Arrays.stream(CaffeineCacheType.values())
                .map(cache -> new CaffeineCache(cache.getCacheName(), Caffeine.newBuilder().recordStats()
                        .expireAfterWrite(cache.getExpiredAfterWrite(), TimeUnit.HOURS)
                        .maximumSize(cache.getMaximumSize())
                        .build()))
                .toList();
    }
    @Bean
    public CacheManager cacheManager(List<CaffeineCache> caffeineCaches) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caffeineCaches);

        return cacheManager;
    }
}
```

### redis
```
@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30L))
                .serializeKeysWith(fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurationMap = new HashMap<>();
        cacheConfigurationMap.put("topItem", defaultConfig.entryTtl(Duration.ofDays(1L)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurationMap)
                .build();

    }
}
```
- ehCache와 redis는 cacheManager를 등록할 때 특정 키에 대한 TTL(time to live)을 따로 설정할 수 있다.
- caffeine은 Enum 으로 설정할 수 있다.

```
@Getter
@RequiredArgsConstructor
public enum CacheType {
    MEMBER_PROFILE("member", 12, 10000);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
```

위와 같이 config를 만들고 @Cacheable로 캐싱을 하고, @CacheEvict로 캐시를 제거한다.
어노테이션 기반으로 '아 어떤 메소드가 호출되면 캐싱이 되는구나' 를 알 수 있다.

---
### 멘토링 반영
- @Ordered 순서
- ![ordered](/src/docs/images/ordered.png)
- @Transactional Ordered 확인
- ![transaction](/src/docs/images/transactionalOrdered.png)
- @DistributedLock Ordered 수정
- ![distributedLock](/src/docs/images/distributedLockAop.png)

