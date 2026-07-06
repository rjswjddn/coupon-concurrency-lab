import http from 'k6/http';
import { Counter } from 'k6/metrics';

// ─────────────────────────────────────────────────────────────
// 선착순 발급 부하 스크립트 (1단계: 락 없음 → oversell 관측용)
//
// 이 파일의 "값"과 "부하 형태"는 전부 knob 이다. 시나리오 설계는
// 측정자(너)의 영역이므로, 아래 값들은 기본치일 뿐 정답이 아니다.
// 실행: k6 run -e VUS=200 -e STOCK=100 load-test/issue.js
// ─────────────────────────────────────────────────────────────

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const JSON_HEADERS = { headers: { 'Content-Type': 'application/json' } };

// ── 시나리오 knob (← 여기가 네가 설계하는 부분) ───────────────
const STOCK = Number(__ENV.STOCK || 100); // 선착순 수량(재고)
const MEMBERS = Number(__ENV.MEMBERS || 500); // 미리 만들 멤버 수(발급 주체 풀)
const VUS = Number(__ENV.VUS || 200); // 동시 가상 유저
const ITERATIONS = Number(__ENV.ITERATIONS || 2000); // 총 발급 시도 수

export const options = {
  // 부하 형태(executor)도 knob. shared-iterations = "정해진 총 시도를
  // VUS개가 최대한 빨리 나눠 던진다" = 순간 스파이크에 가까움.
  // 계단식 램프를 보고 싶으면 ramping-vus 로 바꾸면 된다.
  scenarios: {
    burst: {
      executor: 'shared-iterations',
      vus: VUS,
      iterations: ITERATIONS,
      maxDuration: '60s',
    },
  },
  // 판정 기준(threshold)도 네가 정한다. 예시로 p99 latency만 걸어둔다.
  // 실패율/TPS 목표선을 여기 추가해 "합격/불합격"을 자동 판정할 수 있다.
  thresholds: {
    http_req_duration: ['p(99)<2000'],
  },
};

const issued = new Counter('coupon_issued_201'); // 발급 성공(201) 수
const rejected = new Counter('coupon_rejected'); // 재고소진 등 거절(4xx/5xx) 수

export function setup() {
  // 매 실행마다 "새 정책"을 만들어 실행 간 격리한다(별도 리셋 불필요).
  // 발급 기간은 일부러 아주 넓게 둬서 기간 검증이 측정에 끼어들지 않게 한다
  // — 여기서 재고 못하는 건 오직 재고 경합뿐이어야 관측이 깨끗하다.
  const policyPayload = JSON.stringify({
    name: `load-${STOCK}`,
    discountType: 'AMOUNT',
    discountValue: 1000,
    totalQuantity: STOCK,
    issueStartAt: '2000-01-01T00:00:00',
    issueEndAt: '2999-12-31T23:59:59',
    onePerMember: false, // 1인1매 레이스를 볼 땐 true 로. (2단계 주제)
  });
  const created = http.post(`${BASE}/api/coupon-policies`, policyPayload, JSON_HEADERS);
  if (created.status !== 201) {
    throw new Error(`policy 생성 실패: ${created.status} ${created.body}`);
  }
  const policyId = created.json('id');

  const memberIds = [];
  for (let i = 0; i < MEMBERS; i++) {
    const m = http.post(
      `${BASE}/api/members`,
      JSON.stringify({ name: `m${i}`, email: `m${i}-${policyId}@load.test` }),
      JSON_HEADERS,
    );
    if (m.status === 201) memberIds.push(m.json('id'));
  }
  console.log(`[setup] policyId=${policyId} stock=${STOCK} members=${memberIds.length}`);
  return { policyId, memberIds };
}

export default function (data) {
  // 멤버 배분 전략도 knob. VU마다 다른 멤버 → oversell 관측.
  // 같은 멤버에 몰아주면(예: memberIds[0]) 1인1매 중복발급 레이스가 보인다.
  const memberId = data.memberIds[(__VU + __ITER) % data.memberIds.length];
  const res = http.post(
    `${BASE}/api/coupon-policies/${data.policyId}/issues`,
    JSON.stringify({ memberId }),
    JSON_HEADERS,
  );
  if (res.status === 201) issued.add(1);
  else rejected.add(1);
}

export function teardown(data) {
  // 정합성 검증: 최종 재고. 락이 없으면 음수로 내려간다(= oversell).
  const res = http.get(`${BASE}/api/coupon-policies/${data.policyId}`);
  const finalStock = res.json('stockQuantity');
  console.log('──────── 정합성 결과 ────────');
  console.log(`policyId       = ${data.policyId}`);
  console.log(`재고(설정)      = ${STOCK}`);
  console.log(`최종 stock     = ${finalStock}  (0 미만이면 oversell)`);
  console.log(`(발급 성공/거절 수는 위 coupon_issued_201 / coupon_rejected 메트릭 참고)`);
  console.log('※ 발급 성공 201 수가 재고보다 크면 oversell.');
  console.log('※ 확정 검증은 DB에서: load-test/README.md 의 검증 쿼리 참고');
}
