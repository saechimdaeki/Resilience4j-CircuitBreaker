# Resilience4j 소개

### Resilience4j의 핵심적인 기능들
- CircuitBreaker
- Bulkhead
- RateLimiter
- Retry
- TimeLimiter
- Cache


`### Resilience4j의 핵심적인 기능들 - Retry`

API 요청 -> 서버A ->(일시적 실패한다면?) -> 서버 B 

(서버 B가 배포중일수도 있고 네트워크 작업이 있었을 수도 있음.. 이럴 때 일시적으로 실패)

`### Resilience4j의 핵심적인 기능들 - CircuitBreaker`

API 요청 -> 서버A ->(다시 시도 하는 것으로 해결이 안되고) 트래픽이 너무 많아서 회복을 위해 잠시 트래픽을 차단시켜야 한다면? -> 서버 B 

서버가 너무 트리팩을 많이 받아 회복을 위해 잠시 트래픽을 차단시켜야 한다면? -> 원래 기능을 대체할 수 있는 기능(`fallback`) 실행

### Resilience4j를 활용하기 적절한 상황들

1. 작업이 일시적으로 실패하지만 금방 회복될 가능성이 있는경우 Retry사용 

    - 고려해야할 요소 3가지
      - 몇번까지 재시도 할 것인가?
      - 재시도 간격은 얼마나 길게 줄 것인가?
      - 어떤 상황을 호출 실패로 간주할 것인가? ex) NPE의 경우 일시적 발생이 아닐 확률이크기에 재시도로 하기엔 어렵다


2. CircuitBreaker

    다음과 같은 프로젝트 구조가 있다고 가정하자.

    ![image](./image/circuit%20breaker%20example.png)

    상품목록을 조회하는데 리뷰에 대한 조회가 실패하거나 조회 속도가 느려지는 경우가 발생. 이에 대한 방어 로직이 없다면 api는 에러가 발생할것이다

    이러한 상황이 CircuitBreaker가 사용되기 좋은 예시이다.

    ![image](./image/circuit%20breaker%20example2.png)




### TMI

```yaml
spring:
  application.name: circuit-saechim

server:
  port: 8080


resilience4j.retry:
  configs:
    default:
      maxAttempts: 3
      waitDuration: 1000
      retryExceptions:
        - me.saechimdaeki.circuit.exception.RetryException   # retryExceptions에 지정된 예외는 재시도
      ignoreExceptions:
        - me.saechimdaeki.circuit.exception.IgnoreException  # retryExceptions에 지정되지 않은 예외는 ignoreExceptions로 처리됨
  instances:
    simpleRetryConfig:
      baseConfig: default

resilience4j.circuitbreaker:
  configs:
    default:
      slidingWindowType: COUNT_BASED
      minimumNumberOfCalls: 7                                   # 최소 7번까지는 무조건 CLOSE로 가정하고 호출한다..
      slidingWindowSize: 10                                     # (minimumNumberOfCalls 이후로는) 10개의 요청을 기준으로 판단한다.
      waitDurationInOpenState: 10s                              # OPEN 상태에서 HALF_OPEN으로 가려면 얼마나 기다릴 것인가

      failureRateThreshold: 40                                  # slidingWindowSize 중 몇 %가 recordException이면 OPEN으로 만들 것인가?

      slowCallDurationThreshold: 3000                           # 몇 ms 동안 요청이 처리되지 않으면 실패로 간주할 것인가?
      slowCallRateThreshold: 60                                 # slidingWindowSize 중 몇 %가 slowCall이면 OPEN으로 만들 것인가?

      permittedNumberOfCallsInHalfOpenState: 5                  # HALF_OPEN 상태에서 5번까지는 CLOSE로 가기위해 호출한다.
      automaticTransitionFromOpenToHalfOpenEnabled: true        # OPEN 상태에서 자동으로 HALF_OPEN으로 갈 것인가?

      eventConsumerBufferSize: 10                               # actuator를 위한 이벤트 버퍼 사이즈

      recordExceptions:
        - me.saechimdaeki.circuit.exception.RecordException
      ignoreExceptions:
        - me.saechimdaeki.circuit.exception.IgnoreException
  instances:
    simpleCircuitBreakerConfig:
      baseConfig: default

management.endpoints.web.exposure.include: '*'
management.endpoint.health.show-details: always

management.health.diskspace.enabled: false
management.health.circuitbreakers.enabled: true

management.metrics.tags.application: ${spring.application.name}
management.metrics.distribution.percentiles-histogram.http.server.requests: true
management.metrics.distribution.percentiles-histogram.resilience4j.circuitbreaker.calls: true

```

