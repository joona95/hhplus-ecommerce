import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        baseline_performance: {
            executor: 'ramping-vus',
            startVUs: 10,
            stages: [
                { duration: '10s', target: 10 },   // warm-up
                { duration: '30s', target: 50 },   // 측정 구간
                { duration: '30s', target: 100 },  // 최대 부하 시 안정성 측정
                { duration: '10s', target: 0 },    // 정리
            ],
            exec: 'testScenario',
        }
    }
};

export function testScenario() {
    const res = http.get('http://localhost:8080/api/v1/items/popular');
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 300ms': (r) => r.timings.duration < 300,
    });
}