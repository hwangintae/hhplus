# 부하 테스트 개선 결과

## 1. 개선 내용
- 인기상품 캐시 및 인덱스 적용
    ```
    @Cacheable(value = "popularItemsCache", key = "#from + '_' + #limit", cacheManager = "redisCacheManager")
    public List<PopularItemsResult> getPopularItems(int from, int limit) {
        return orderItemRepository.findPopularItems(from, limit);
    }
    ```
    ```
    create index idx_ordered_at_item_id on order_item(ordered_at, item_id);
    ```
- 주문 상태 조회 인덱스 적용 
    ```
    create index idx_orders_id on order_item(orders_id);
    ```
  

## 2. 개선 결과 성능 테스트

### 2.1 분석
- Checks: 100.00% : 모든 테스트 성공
- http_req_duration

  | **구분** | **값**     | **설명**                            |
    |--------|------------|------------------------------------|
  | avg    | 2.46ms   | 평균 소요 시간입니다.             |
  | P50    | 1.45ms     | 중앙값(50% 이하의 요청 시간)입니다.   |
  | P90    | 4.21ms     | 90%의 요청이 4.21ms 이내에 처리되었습니다. |
  | P95    | 6ms     | 95%의 요청이 6ms 이내에 처리되었습니다. |
  | max    | 640.57ms      | 가장 오래 걸린 요청의 소요 시간입니다.   |


### 2.2 log
![resultLog](/src/docs/images/test/resultLog.png)

### 2.3 Dash board
![resultDashboard](/src/docs/images/test/resultDashboard.png)