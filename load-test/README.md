# 부하 테스트 / 측정 하네스

이 디렉터리는 **하네스**다. 도구는 여기 있지만, **시나리오 설계와 결과 해석은 측정자의 몫**이다.
(값을 바꿔 가며 가설을 검증하는 게 학습의 본체다. 아래 기본값은 정답이 아니라 출발점.)

## 준비물

- [k6](https://k6.io/docs/get-started/installation/) 설치 (`winget install k6` 또는 `choco install k6`)
- MySQL 컨테이너 실행: `docker compose up -d`
- 앱 실행: `./gradlew bootRun`

## 실행

```bash
# 기본값으로
k6 run load-test/issue.js

# knob 조절 (재고 100, 동시 VU 300, 총 2000회 시도)
k6 run -e STOCK=100 -e VUS=300 -e ITERATIONS=2000 load-test/issue.js
```

## 지금 볼 수 있는 knob (= 네가 설계하는 축)

| env      | 의미                     | 실험 아이디어 |
|----------|--------------------------|--------------|
| `STOCK`  | 선착순 수량(재고)         | 작게(10) 잡을수록 경합이 격해져 oversell이 잘 보인다 |
| `VUS`    | 동시 가상 유저            | 재고 대비 몇 배로 몰아칠지 |
| `ITERATIONS` | 총 발급 시도 수       | VUS와 함께 부하 총량 결정 |
| `MEMBERS`| 발급 주체 풀 크기         | 1인1매 레이스를 볼 땐 작게(=같은 멤버 재사용) |
| `BASE_URL` | 앱 주소                | 기본 `http://localhost:8080` |

`issue.js` 안의 `executor`(부하 형태)와 `onePerMember`, 멤버 배분식도 knob이다.
- **oversell 관측**: `onePerMember:false` + 멤버 넉넉히 → 서로 다른 유저가 재고를 놓고 경합
- **1인1매 중복발급 관측(2단계 주제)**: `onePerMember:true` + `MEMBERS=1` → 같은 유저 따닥

## 판정 기준 (= 네가 정하는 것)

k6가 뽑아주는 수치 중 이 프로젝트가 보는 것:
- **TPS** = `http_reqs` rate
- **p99 latency** = `http_req_duration p(99)` (평균 말고 p99를 본다)
- **실패율** = `http_req_failed`
- **oversell** = 발급 성공 201 수 − 재고, 또는 최종 stock의 음수 폭

> 로컬 단일 머신이라 **절대 수치는 무의미**하다. 같은 환경(docker 리소스 고정)에서
> 단계 간 **상대 비교**로만 해석한다.

## 정합성 확정 검증 (DB ground-truth)

teardown 로그의 `최종 stock`이 1차 신호지만, 확정은 DB에서 센다.
`k6` 로그에 찍힌 `policyId`를 넣고:

```sql
-- 실제 발급된 인스턴스 수 vs 설정 재고
SELECT
  p.id,
  p.total_quantity            AS 설정재고,
  p.stock_quantity            AS 최종stock,
  COUNT(ic.id)                AS 실제발급수,
  COUNT(ic.id) - p.total_quantity AS oversell_수
FROM coupon_policy p
LEFT JOIN issued_coupon ic ON ic.coupon_policy_id = p.id
WHERE p.id = :policyId
GROUP BY p.id, p.total_quantity, p.stock_quantity;

-- 1인1매 중복발급 관측용 (같은 멤버가 여러 장 받았는지)
SELECT member_id, COUNT(*) AS 발급수
FROM issued_coupon
WHERE coupon_policy_id = :policyId
GROUP BY member_id
HAVING COUNT(*) > 1;
```

컨테이너에서 바로:
```bash
docker exec -it coupon-mysql mysql -ucoupon -pcoupon coupon -e "여기에 위 쿼리"
```

## 산출물

각 단계 종료 시 **가설 → 측정 결과 → 진단 → 결정**을 짧게 기록한다.
면접관이 읽는 건 코드가 아니라 이 기록이다. (README든 블로그든)
```
