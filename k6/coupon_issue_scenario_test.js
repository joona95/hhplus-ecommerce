import http from 'k6/http';
import {check, group, fail } from 'k6';

export const options = {
    executor: 'ramping-arrival-rate',
    startRate: 0,
    stages: [
        { duration: "1m", target: 2700 },
        { duration: "30s", target: 6500 },
        { duration: "1m", target: 2700 },
        { duration: "30s", target: 6500 },
        { duration: "1m", target: 2700 },
        { duration: "30s", target: 6500 },
        { duration: "1m", target: 2700 },
        { duration: "1m", target: 0 }
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],  // 95% 이상의 요청이 500ms 이하 유지
        http_req_failed: ["rate<0.05"],    // 실패율 5% 미만 유지
    },
};

export default function () {
    group('Peak Test Scenario', function () {
        const userId = Math.floor(Math.random() * 1000000) + 1;
        const couponId = 3;

        group('Issue coupon', function () {
            return issueCoupon(userId, couponId);
        });
    })
}

function issueCoupon(userId, couponId) {

    const headers = {
        'Content-Type': 'application/json',
        'X-USER-ID': String(userId)
    };

    const url = `http://localhost:8080/api/v1/coupons`;

    const payload = JSON.stringify({
        couponId: couponId
    });

    const res = http.post(url, payload, { headers: headers });

    const isOK = check(res, {'API success': (r) => r.status === 200});
    if (isOK) {
        console.log(`Coupon Issue successful for user ID: ${userId}, coupon ID: ${couponId}`);
    } else {
        console.log(`Coupon Issue failed. user ID: ${userId}, coupon ID: ${couponId} ,  Status: ${res.status}`);
        fail(`test stopped. Coupon Issue failed.`);
    }
    return res;
}
