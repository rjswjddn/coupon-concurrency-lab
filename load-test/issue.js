import http from 'k6/http';
import { Counter } from 'k6/metrics';

// ═══════════════════════════════════════════════════════════════
//  설정 — 여기 값만 바꾸면 된다
// ═══════════════════════════════════════════════════════════════

// 서버 주소
const BASE = 'http://localhost:8080';

// 동시에 발급을 요청할 유저 수 (각 유저가 한 번씩 요청)
const USER_COUNT = 100;

// 이번 실행에서 만들 정책
const POLICY = {
  name: 'load-test-coupon',
  discountType: 'AMOUNT', // 'AMOUNT' | 'RATE'
  discountValue: 1000,
  totalQuantity: 10, // 선착순 수량(재고)
  issueStartAt: '2000-01-01T00:00:00', // 넓게 열어 기간 검증이 끼어들지 않게
  issueEndAt: '2999-12-31T23:59:59',
  onePerMember: false, // 한 명이 한 장만 받을 수 있는지
};

// ═══════════════════════════════════════════════════════════════
//  아래는 위 설정으로 자동 구성된다 (보통 건드릴 일 없음)
// ═══════════════════════════════════════════════════════════════

const JSON_HEADERS = { headers: { 'Content-Type': 'application/json' } };

export const options = {
  // USER_COUNT 명이 각자 한 번씩, 최대한 동시에 요청한다.
  scenarios: {
    burst: {
      executor: 'shared-iterations',
      vus: USER_COUNT,
      iterations: USER_COUNT,
      maxDuration: '60s',
    },
  },
  thresholds: {
    http_req_duration: ['p(99)<2000'],
  },
};

const issued = new Counter('coupon_issued_201'); // 발급 성공(201) 수
const rejected = new Counter('coupon_rejected'); // 거절(4xx/5xx) 수

export function setup() {
  // 1) 정책 생성 (매 실행마다 새 정책 → 실행 간 격리)
  const created = http.post(`${BASE}/api/coupon-policies`, JSON.stringify(POLICY), JSON_HEADERS);
  if (created.status !== 201) {
    throw new Error(`정책 생성 실패: ${created.status} ${created.body}`);
  }
  const policyId = created.json('id');

  // 2) 미리 넣어둔 멤버를 조회해 USER_COUNT명 확보 (멤버 생성은 하지 않음)
  const memberIds = [];
  const pageSize = 100; // 목록 API 최대 페이지 크기
  for (let page = 0; memberIds.length < USER_COUNT; page++) {
    const res = http.get(`${BASE}/api/members?page=${page}&size=${pageSize}`);
    const content = res.json('content');
    if (!content || content.length === 0) break; // 더 가져올 멤버가 없음
    for (const m of content) {
      memberIds.push(m.id);
      if (memberIds.length >= USER_COUNT) break;
    }
  }
  if (memberIds.length < USER_COUNT) {
    throw new Error(`멤버 부족: 필요 ${USER_COUNT}명, 실제 ${memberIds.length}명. 더미 멤버를 먼저 넣으세요.`);
  }
  console.log(`[setup] policyId=${policyId} 재고=${POLICY.totalQuantity} 유저=${memberIds.length}`);
  return { policyId, memberIds };
}

export default function (data) {
  // VU 하나 = 유저 하나. 각자 자기 멤버로 한 번 발급 요청.
  const memberId = data.memberIds[(__VU - 1) % data.memberIds.length];
  const res = http.post(
    `${BASE}/api/coupon-policies/${data.policyId}/issues`,
    JSON.stringify({ memberId }),
    JSON_HEADERS,
  );
  if (res.status === 201) issued.add(1);
  else rejected.add(1);
}

export function teardown(data) {
  // 최종 재고 확인
  const res = http.get(`${BASE}/api/coupon-policies/${data.policyId}`);
  const finalStock = res.json('stockQuantity');
  console.log('──────── 결과 ────────');
  console.log(`policyId    = ${data.policyId}`);
  console.log(`유저 수      = ${USER_COUNT}`);
  console.log(`재고(설정)   = ${POLICY.totalQuantity}`);
  console.log(`최종 stock  = ${finalStock}`);
  console.log('(발급 성공/거절 수는 coupon_issued_201 / coupon_rejected 메트릭 참고)');
}
