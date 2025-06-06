import http from 'k6/http';
import {check, group, fail } from 'k6';

export const options = {
    executor: 'ramping-arrival-rate',
    startRate: 0,
    stages: [
        { duration: "1m", target: 2700 },
        { duration: "3m", target: 2700 },
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

        const item = group('Fetch Item', function () {
            return getItem(userId);
        });
        if (!item) {
            console.log('No item data.');
            return;
        }
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
