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