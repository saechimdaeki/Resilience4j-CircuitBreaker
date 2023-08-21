package me.saechimdaeki.circuit.a_retry

import io.github.resilience4j.retry.annotation.Retry
import me.saechimdaeki.circuit.exception.RetryException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
@Service
class RetryService {

    private val log = LoggerFactory.getLogger(this::class.java)


    @Retry(name = SIMPLE_RETRY_CONFIG, fallbackMethod = "fallback")
    fun process(param : String) : String?{
        return callAnotherServer(param)
    }

    fun fallback(param:String, ex: Exception) : String {
        log.info("fallback your request is $param")
        return "Recoverd $ex"
    }

    private fun callAnotherServer(param: String) :String? {
        log.info("callAnotherServer $param")
        throw RetryException("retry Exception")
    }


    companion object {
        const val SIMPLE_RETRY_CONFIG = "simpleRetryConfig"
    }
}