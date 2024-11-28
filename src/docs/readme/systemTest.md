# 부하 테스트 문서

## 개요
> 황인태요 이커머스는 많은 사용자가 동시에 접근하여도 안정적으로 시스템이 작동해야한는 목표를 가지고 있다. 따라서, 부하
> 테스트를 통해 시스템의 병목 현상을 확인하고 개선하는데 목적이 있다.

## 1. 부하 테스트 대상 선정 기준
  > 1. 사용자가 빈번하게 사용할 api
  > 2. 잔액 충전, 상품 주문 등 동시성 문제가 발생하면 안되는 api
  > 3. slow query가 발생할 수 있는 통계성 데이터 조회

## 2. 테스트 대상 목록
> 위의 기준으로 테스트 대상을 다음과 같이 선정하였다.

| **기능명**           | **Method** | **Endpoint**                     | **설명**                                       |
|-----------------------|------------|-----------------------------------|-----------------------------------------------|
| 인기 상품 조회        | GET        | `/api/orders/popular`            | 인기 있는 상품 목록을 조회합니다.              |
| 상품 목록 조회        | GET        | `/api/items?itemIds`             | 특정 상품 ID 리스트를 기반으로 상품 목록을 조회합니다. |
| 장바구니에 상품 추가  | POST       | `/api/cart`                      | 장바구니에 상품을 추가합니다.                  |
| 잔액 충전             | POST       | `/api/cash`                      | 사용자의 잔액을 충전합니다.                    |
| 상품 주문 요청        | POST       | `/api/orders`                    | 상품 주문을 요청합니다.                        |
| 주문 상태 조회        | GET        | `/api/orders/status`             | 주문 상태를 조회합니다.                        |

## 3. 테스트 목표
> 1. 테스트 대상 목록의 성능 지표 평가(응답시간, 처리량, 에러율)
> 2. 병목 현상 확인

## 4. 테스트 시나리오
> - 한정된 리소스에서 테스트를 진행하므로 VUser의 수를 최대 100명으로 진행하였다.
> - VUser의 수를 0에서 시작하여 50 -> 100 -> 50 -> 0 순으로 설정하였다.

## 5. 하드웨어 성능
> CPU는 apple m2이며 도커를 사용하여 8 코어로 제한을 두었고, RAM은 최대 8GB 로 설정하였다.

## 6. 테스트 스크립트 (k6)
```js
import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    scenarios: {
        ecommerce_test: {
            executor: 'ramping-vus',
            startVus: 0,
            stages: [
                {duration: '10s', target: 50},
                {duration: '10s', target: 100},
                {duration: '10s', target: 50},
                {duration: '10s', target: 0},
            ],
        }
    },
};

const BASE_URL = 'http://localhost:8080';

const getPopularItems = () => {

    let res = http.get(`${BASE_URL}/api/orders/popular?from=7&limit=5`, {
        tags: {
            name: "Step 1: 인기 상품 조회"
        }
    });
    check(res, {
        '인기 상품 조회 성공': (r) => r.status === 200,
    });

    return res;
}

const getItems = (itemIds) => {

    let res = http.get(`${BASE_URL}/api/items?itemIds=${itemIds.join(',')}`, {
        tags: {
            name: "Step 2: 상품 목록 조회"
        }
    });
    check(res, {
        '상품 목록 조회 성공': (r) => r.status === 200,
    });

    return res;
}

const addCart = (userId, cartItemCreate) => {

    let json = JSON.stringify({
        userId: userId,
        cartItemCreate: cartItemCreate
    })

    let res = http.post(`${BASE_URL}/api/cart`, json
        , {
            headers: {'Content-Type': 'application/json'},
            tags: {
                name: "Step 3: 장바구니에 상품 추가"
            }
        });
    check(res, {
        '장바구니에 상품 추가 성공': (r) => r.status === 200,
    });
}

const addCash = (userId, amount) => {

    let res = http.post(`${BASE_URL}/api/cash`,
        JSON.stringify({userId: userId, amount: amount}), {
            headers: {'Content-Type': 'application/json'},
            tags: {
                name: "Step 4: 잔액 충전"
            }
        },
    );
    check(res, {
        '잔액 충전 성공': (r) => r.status === 200,
    });
}

const createOrders = (userId, orderRequests) => {

    let res = http.post(`${BASE_URL}/api/orders`,
        JSON.stringify({userId: userId, orderRequests: orderRequests}), {
            headers: {'Content-Type': 'application/json'},
            tags: {
                name: "Step 5: 상품 주문 요청"
            }
        });
    check(res, {
        '상품 주문 요청 성공': (r) => r.status === 200,
    });

    return res;
}

const getOrders = (ordersId) => {

    let res = http.get(`${BASE_URL}/api/orders/status?ordersId=${ordersId}`, {
        tags: {
            name: "Step 6: 주문 상태 조회"
        }
    });
    let responseBody = JSON.parse(res.body);
    check(res, {
        '주문 상태 조회 성공': (r) => r.status === 200,
    });

    return res;
}

export default function () {
    const userId = Math.floor(Math.random() * 50) + 1;

    // Step 1: 인기 상품 조회
    const popularResponse = getPopularItems();
    if (!popularResponse) return

    const popularItems = JSON.parse(popularResponse.body);

    const itemIds = popularItems.map((item) => item.itemId);

    // Step 2: 상품 목록 조회
    const itemsResponse = getItems(itemIds);
    if (!itemsResponse) return

    const itemsMap = JSON.parse(itemsResponse.body);
    const items = itemsMap.data;
    const itemIdAndCnts = items.map((item) => ({
        itemId: item.id,
        cnt: 1,
    }));

    // Step 3: 장바구니에 상품 추가
    itemIdAndCnts.forEach(itemIdAndCnt => addCart(userId, itemIdAndCnt))

    // Step 4: 잔액 충전
    const chargeAmount = 20000;
    addCash(userId, chargeAmount);

    // Step 5: 상품 주문 요청
    const orderResponse = createOrders(userId, itemIdAndCnts);
    if (!orderResponse) return

    const orders = JSON.parse(orderResponse.body);

    // Step 6: 주문 상태 조회
    const statusResponse = getOrders(orders.data);

    sleep(2);
}
```

## 7. 테스트 결과

### 7.1 분석
- Checks: 100.00% : 모든 테스트 성공
- http_req_duration

  | **구분** | **값**     | **설명**                            |
  |--------|------------|------------------------------------|
  | avg    | 680.46ms   | 평균 소요 시간입니다.             |
  | P50    | 8.16ms     | 중앙값(50% 이하의 요청 시간)입니다.   |
  | P90    | 2.87초     | 90%의 요청이 2.87초 이내에 처리되었습니다. |
  | P95    | 4.66초     | 95%의 요청이 4.66초 이내에 처리되었습니다. |
  | max    | 9.5초      | 가장 오래 걸린 요청의 소요 시간입니다.   |
- slow query 발생
  - 인기 상품 조회 (최대: 8초, 평균: 4초)
  - 주문 상태 조회 (최대: 6초, 평균: 2초)

### 7.2 log
![testLog](/src/docs/images/test/testLog.png)

### 7.3 Dash board
![grafana1](/src/docs/images/test/grafana1.png)

## 개선방안
- 조회되는 데이터량이 많아 캐싱과 인덱스를 활용하여 데이터베이스 부하를 개선
  - 캐싱 : 인기 상품 조회
  - 인덱스 추가 : 주문 상태 조회