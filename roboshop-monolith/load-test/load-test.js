import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { SharedArray } from 'k6/data';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
    stages: [
        { duration: '30s', target: 10 },  // ramp up
        { duration: '1m',  target: 20 },  // steady
        { duration: '30s', target: 50 },  // spike - demonstrates vertical scaling need
        { duration: '30s', target: 0 },   // ramp down
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'],
        http_req_failed: ['rate<0.1'],
    },
};

export default function () {
    const uniqueId = `${__VU}_${__ITER}_${Date.now()}`;
    const username = `loadtest_${uniqueId}`;
    const password = 'TestPass123';
    const jsonHeaders = { headers: { 'Content-Type': 'application/json' } };

    // k6 automatically manages cookies per VU (session cookie)

    group('Browse Catalogue', function () {
        let res = http.get(`${BASE_URL}/api/catalogue/products`);
        check(res, { 'catalogue 200': (r) => r.status === 200 });

        res = http.get(`${BASE_URL}/api/catalogue/categories`);
        check(res, { 'categories 200': (r) => r.status === 200 });

        res = http.get(`${BASE_URL}/api/catalogue/products/search?q=robot`);
        check(res, { 'search 200': (r) => r.status === 200 });
    });

    group('View Product', function () {
        let productId = Math.floor(Math.random() * 12) + 1;
        let res = http.get(`${BASE_URL}/api/catalogue/products/${productId}`);
        check(res, { 'product detail 200': (r) => r.status === 200 });
    });

    group('Register', function () {
        let res = http.post(`${BASE_URL}/api/user/register`, JSON.stringify({
            username: username,
            email: `${username}@test.com`,
            password: password,
            firstName: 'Load',
            lastName: 'Test',
        }), jsonHeaders);
        check(res, { 'register ok': (r) => r.status === 200 || r.status === 201 });
    });

    group('Login', function () {
        let res = http.post(`${BASE_URL}/api/user/login`, JSON.stringify({
            username: username,
            password: password,
        }), jsonHeaders);
        check(res, { 'login 200': (r) => r.status === 200 });
    });

    group('Add to Cart', function () {
        let res = http.post(`${BASE_URL}/api/cart/add`, JSON.stringify({
            productId: Math.floor(Math.random() * 12) + 1,
            quantity: 1,
        }), jsonHeaders);
        check(res, { 'add to cart ok': (r) => r.status === 200 });
    });

    group('View Cart', function () {
        let res = http.get(`${BASE_URL}/api/cart`);
        check(res, { 'view cart 200': (r) => r.status === 200 });
    });

    group('Checkout', function () {
        let res = http.post(`${BASE_URL}/api/orders`, JSON.stringify({
            cityId: Math.floor(Math.random() * 25) + 1,
        }), jsonHeaders);
        // May fail if not logged in via session - that's expected behavior to measure
        check(res, { 'checkout attempted': (r) => r.status < 500 });
    });

    sleep(1);
}
