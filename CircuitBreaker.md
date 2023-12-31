## CircuitBreaker주요 설정 및 설명

#### SlidingWindowType

`Sliding Window`는 COUNT_BASED가 default설정이고 TIME_BASED설정도 있음

![image](image/SlidingWindowType.png)

횟수를 기준으로 sliding window를 지정하는것이 COUNT_BASED 타입


#### SlidingWindowSize

windowSize는 default가 100

윈도우 사이즈가 100 이면 100개의 요청의 성공과 실패를 기준으로 서킷브레이커의 상태를 변경하는데 판단함.

만약 timebased라면 windowSize는 초(second)

#### failureRateThreshold

슬라이딩 윈도우에서 몇%를 실패해야 서킷이 열릴지 기준치. default는 50

#### minimumNumberOfCalls

슬라이딩 윈도우의 사이즈가 10이고 실패 임계치가 70인데 윈도우가 채워지지 않은 상태라고 해보자

![image](image/minimumNumberOfCalls.png)

슬라이딩 윈도우가 가득 차지 않았기에 아직 서킷의 상태를 변경하고 싶지 않다. 그렇다면 minimumNumberOfCalls이 옵션을 두는 것이다.

minimumNumberOfCalls를 두면 해당 갯수를 넘어간 요청부터 윈도우 사이즈의 실패율을 계산한다. default는 100 

`주의할점은 sliding window보다 큰 값으로 지정하면 당연히 의미가 없다 `

#### waitDurationInOpenSate

서킷의 상태가 open으로 바뀐다음 open에 얼마동안 머물러 있을지에 대한 설정

![image](image/waitDurationInOpenSate.png)

#### permittedNumberOfCallsInHalfOpenState

halfOpen상태에서 정상적인 상태로 갈 수 있는지 몇번까지 확인하는지 요청을 해볼지에 대한 설정

![image](image/permittedNumberOfCallsInHalfOpenState.png)


#### automaticTransitionFromOpenToHalfOpenEnabled

open상태에서 waitDurationInOpenSate만큼 대기후 자동으로 상태를 halfopen상태로 만들지 아닐지에 대한 설정

true일시 자동으로 halfopen으로 변경하고 false로 지정하면 요청이 한번 들어올때 

waitDurationInOpenSate만큼 지났다면 Halfopen으로 변경후 요청처리

![image](image/automaticTransitionFromOpenToHalfOpenEnabled.png)


#### slowCallDurationThreshold

느린 호출로 간주할 시간을 지정하는 설정 (느린요청으로 sliding window에 기록)

#### slowCallRateThreshold

실패율과 마찬가지로 느린호출이 몇 이상이 되면 서킷의 상태를 Open으로 할지에 대한 설정



----

### 어떤 예외를 recordException으로 지정할까?

recordException은 실패라고 간주하여 시스템을 회복시키기 위해 트래픽을 차단할 필요가 있는 상황에 던져지는 예외를 지정해야함.

![image](image/recordException.png)

즉, 보호하려는 대상에서 어떤 예외가 던져지는지 알아야함

![image](image/boho.png)


#### `다만 주의해야할 점이 있다`

- 유효성 검사나 NullPointerException처럼 서킷이 열리는 것과 무관한 예외는 recordException로 등록 X
- Exception이나 RuntimeException처럼 너무 높은 수준의 예외 역시 recordException로 등록 X


`만약 라이브러리가 예외를 던져주지 않는다면?`

![image](image/502bad.png)

이런상황에서는 500번대의 에러라면 직접 예외를 던져주는 것이 좋다. (recordException으로 걸어서)

`Slow Call에만 의존하지 말자`

![image](image/slowCall.png)

---

### fallback 활용하기

![image](image/fallback.png)

`fallback Method로 할 수 있는 것`

![image](image/canFallback.png)