package me.saechimdaeki.circuit.c_circuitbreaker_event

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/"])
class CircuitBreakerController(private val circuitBreakerService: CircuitBreakerService) {

    @GetMapping("/api-call")
    fun apiCall(@RequestParam param: String): String {
        return circuitBreakerService.process(param) ?: "fallback"
    }
}
;