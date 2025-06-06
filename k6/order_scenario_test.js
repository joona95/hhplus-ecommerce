import http from 'k6/http';
import {check, sleep, group, fail } from 'k6';

export const options = {
    executor: 'ramping-arrival-rate',
    startRate: 0,
    stages: [
        { duration: "1m", target: 325 },
        { duration: "3m", target: 325 },
        { duration: "1m", target: 0 }
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],  // 95% 이상의 요청이 500ms 이하 유지
        http_req_failed: ["rate<0.05"],    // 실패율 5% 미만 유지
    },
};

export default function () {
    group('Load Test Scenario', function () {
        const userId = Math.floor(Math.random() * 1000000) + 1;

        // 1. 상품 상세 조회
        const item = group('Fetch Item', function () {
            return getItem(userId);
        });
        if (!item) {
            console.log('No item data.');
            return;
        }

        sleep(2);

        // 2. 보유 쿠폰 조회
        const userCouponList = group('Fetch User Coupon List', function () {
            return getUserCouponList(userId);
        });
        if (!userCouponList || userCouponList.length === 0) {
            console.log('No User Coupon data.');
            return;
        }

        sleep(2);

        // 3. 주문
        const orderRes = group('Create Order', function () {
            const couponId = getRandomCoupon(userCouponList);

            const orderItemList = createOrderItemList();

            return order(userId, couponId, orderItemList);
        });
    })
}

function getItem(userId) {

    const itemId = Math.floor(Math.random() * 1000000) + 1;
    const url = `http://localhost:8080/api/v1/items/` + itemId;
    const res = http.get(url);

    const isOK = check(res, {'API success': (r) => r.status === 200});
    if (isOK) {
        console.log(`Item Search successful for user ID: ${userId}`);
    } else {
        console.log(`Item Search failed. user ID: ${userId}, Status: ${res.status}`);
        fail(`test stopped. Item Search failed.`);
    }
    return res;
}

function getUserCouponList(userId) {
    const headers = {
        'Content-Type': 'application/json',
        'X-USER-ID': String(userId)
    };

    const url = `http://localhost:8080/api/v1/coupons`;
    const res = http.get(url, { headers: headers });

    const isOK = check(res, {'API success': (r) => r.status === 200});
    if (isOK) {
        console.log(`User Coupon Search successful for user ID: ${userId}`);
    } else {
        console.log(`User Coupon Search failed. user ID: ${userId}, Status: ${res.status}`);
        fail(`test stopped. User Coupon Search failed.`);
    }
    return res;
}

function order(userId, couponId, orderItemList) {
    const headers = {
        'Content-Type': 'application/json',
        'X-USER-ID': String(userId)
    };

    const url = `http://localhost:8080/api/v1/orders`;

    const payload = JSON.stringify({
        userId: userId,
        couponId: couponId,
        items: orderItemList
    });

    const res = http.post(url, payload, { headers: headers });

    const isOK = check(res, {'API success': (r) => r.status === 200});
    if (isOK) {
        console.log(`Create Order successful for user ID: ${userId}`);
    } else {
        console.log(`Create Order failed. user ID: ${userId}, Status: ${res.status}`);
        fail(`test stopped. Create Order failed.`);
    }

    return res;
}

function getRandomCoupon(couponList) {
    couponList = couponList.json().data;
    if (!couponList || couponList.length === 0) {
        return null;
    }

    if (Math.random() < 0.3) {
        return null;
    }

    return couponList[Math.floor(Math.random() * couponList.length)].couponId;
}

function createOrderItemList() {

    const itemIds = getRandomItemIds();

    return itemIds.map(itemId => ({
        itemId: itemId,
        count: 1,
    }));
}

function getRandomItemIds() {

    const maxSelectable = Math.min(3, 10);

    const candidates = Array.from({length: maxSelectable}, (_, i) => i + 1);

    for (let i = 1; i <= maxSelectable; i++) {
        candidates[i] = Math.floor(Math.random() * maxSelectable) + 1;
    }

    return candidates;
}