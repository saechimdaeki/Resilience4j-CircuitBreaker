package me.saechimdaeki.circuit.b_retry_without_resilience4j

import me.saechimdaeki.circuit.exception.RetryException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RetryService {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val WAIT_DURATION = 1000L
    private val MAX_RETRY = 3

    fun process(param : String) : String?{
        var result:String? = null

        var retryCount = 0
        while (retryCount++ < MAX_RETRY) {
            try {
                result = callAnotherServer(param)
            } catch (e: RetryException) {
                if(retryCount == MAX_RETRY)
                    return fallback(param, e)
                Thread.sleep(WAIT_DURATION)
            }
            if (result != null)
                break
        }
        return result
    }

    private fun fallback(param:String, ex: Exception) : String {
        log.info("fallback your request is $param")
        return "Recoverd $ex"
    }

    private fun callAnotherServer(param: String) :String? {
        log.info("callAnotherServer $param")
        throw RetryException("retry Exception")
    }
}