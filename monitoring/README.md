# 모니터링 (Prometheus + Grafana)

k6가 **판정**(클라이언트 관점 TPS/p99/실패율)이라면, 여기는 **진단**이다.
"병목이 *어디*냐"를 서버 내부 지표로 본다. 단계별로 봐야 할 지표가 다르다.

## 구성

```
host:  ./gradlew bootRun        (측정 대상 앱, :8080, /actuator/prometheus 노출)
docker: mysql (:3306, 2CPU/1G 고정)  ← 측정 대상
        mysqld-exporter (:9104)      ┐
        prometheus (:9090)           ├ 관측자 (리소스 제한 없음)
        grafana (:3000)              ┘
```

앱은 host에서 돌고 Prometheus는 컨테이너다. 그래서 스크레이프 타깃이
`host.docker.internal:8080` (compose에 `host-gateway`로 매핑해 둠).

## 실행

```bash
docker compose up -d            # mysql + 모니터링 3종
./gradlew bootRun               # 앱 (host)
k6 run load-test/issue.js       # 부하
```

- Grafana: http://localhost:3000 (anonymous 켜둠, 로그인 admin/admin)
  → 대시보드 **"Coupon Concurrency Lab"** 자동 프로비저닝됨.
- Prometheus: http://localhost:9090
- 앱 원본 메트릭 확인: http://localhost:8080/actuator/prometheus

## 왜 scrape_interval = 1s 인가

`shared-iterations` 버스트는 수십 초 만에 끝난다. 기본 15s로 긁으면
테스트가 끝난 뒤라 곡선이 안 남는다. 그래서 1s로 촘촘히 긁는다.
(로컬 학습용이라 감당 가능. 운영값이 아니다.)

## 단계별로 볼 패널

| 단계 | 가설(로드맵) | 봐야 할 패널 |
|------|--------------|--------------|
| 1 락없음 | oversell 발생 | (진단보다 k6 teardown / DB 정합성 쿼리) |
| 2 비관락 | 커넥션 풀 고갈로 TPS↓ | **HikariCP connections** 의 `pending` 급등, `active`가 `max`에 붙음 / **acquire time** 상승 |
| 3 별도테이블 | 락 사정거리↓ but 카운터 경합 그대로 | **InnoDB row locks** (위치가 바뀌어도 대기는 그대로인지) |
| 4 원자적 UPDATE | 락 없이 원자성 | row lock 대기 사라지는지 + latency 회복 |
| 5 Redis | TPS 점프 | (Redis exporter는 그때 추가) |

> 로컬 단일 머신이라 **절대치는 무의미**. 같은 환경에서 단계 간 **상대 비교**로만 읽는다.

## 주의

- 대시보드/데이터소스는 provisioning으로 코드화돼 있다. Grafana UI에서
  고쳐도 되지만, 영구 반영하려면 `dashboards/coupon-lab.json`을 고쳐라.
- mysqld-exporter는 root로 붙는다(로컬 전용). 운영에 그대로 쓰지 말 것.
- 5단계에서 Redis 붙이면 `redis_exporter` 스크레이프 job 하나 추가하면 된다.
