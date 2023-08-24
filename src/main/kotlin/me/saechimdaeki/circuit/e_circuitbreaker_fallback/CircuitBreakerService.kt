package me.saechimdaeki.circuit.e_circuitbreaker_fallback

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import me.saechimdaeki.circuit.exception.IgnoreException
import me.saechimdaeki.circuit.exception.RecordException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class CircuitBreakerService {

    private val log = LoggerFactory.getLogger(this::class.java)
    @CircuitBreaker(name = SIMPLE_CIRCUIT_BREAKER_CONFIG, fallbackMethod = "fallback")

    fun process(param: String): String {
        return callAnotherServer(param)
    }

    private fun fallback(param: String, ex: RecordException): String {
        log.info("RecordException fallback! your request is $param")
        return "Recovered: $ex"
    }

    private fun fallback(param: String, ex: IgnoreException): String {
        log.info("IgnoreException fallback! your request is $param")
        return "Recovered: $ex"
    }

    private fun fallback(param: String, ex: CallNotPermittedException): String {
        log.info("CallNotPermittedException fallback! your request is $param")
        return "Recovered: $ex"
    }

    private fun callAnotherServer(param: String): String {
        when (param) {
            "a" -> throw RecordException("record exception")
            "b" -> throw IgnoreException("ignore exception")
            "c" // 3초 이상 걸리는 경우도 실패로 간주
            -> Thread.sleep(4000)
        }
        return param
    }

    companion object {
        private const val SIMPLE_CIRCUIT_BREAKER_CONFIG = "simpleCircuitBreakerConfig"
    }
}
