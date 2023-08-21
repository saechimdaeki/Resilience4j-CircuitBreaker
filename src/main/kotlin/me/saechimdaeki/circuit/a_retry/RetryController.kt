package me.saechimdaeki.circuit.a_retry

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/"])
class RetryController(private val retryService: RetryService) {
    @GetMapping("/api-call")
    fun apiCall(@RequestParam param: String): String {
        return retryService.process(param) ?: "fallback"
    }
}
;