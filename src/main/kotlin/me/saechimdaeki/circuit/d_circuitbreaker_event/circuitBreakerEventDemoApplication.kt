package me.saechimdaeki.circuit.d_circuitbreaker_event

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.core.registry.EntryAddedEvent
import io.github.resilience4j.core.registry.EntryRemovedEvent
import io.github.resilience4j.core.registry.EntryReplacedEvent
import io.github.resilience4j.core.registry.RegistryEventConsumer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class CircuitBreakerEventDemoApplication {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun myRegistryEventConsumer(): RegistryEventConsumer<CircuitBreaker> {
        return object : RegistryEventConsumer<CircuitBreaker> {
            override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<CircuitBreaker>) {
                log.info("RegistryEventConsumer.onEntryAddedEvent")
                val eventPublisher = entryAddedEvent.addedEntry.eventPublisher
                eventPublisher.onEvent { event ->
                    log.info(
                        "onEvent {}",
                        event
                    )
                }
                eventPublisher.onSuccess { event ->
                    log.info(
                        "onSuccess {}",
                        event
                    )
                }
                eventPublisher.onCallNotPermitted { event ->
                    log.info("onCallNotPermitted {}", event
                    )
                }
                eventPublisher.onError { event ->
                    log.info("onError {}", event)
                }
                eventPublisher.onIgnoredError { event ->
                    log.info("onIgnoredError {}", event)
                }
                eventPublisher.onStateTransition { event ->
                    log.info("onStateTransition {}", event)
                }
                eventPublisher.onSlowCallRateExceeded { event ->
                    log.info("onSlowCallRateExceeded {}", event)
                }
                eventPublisher.onFailureRateExceeded { event ->
                    log.info("onFailureRateExceeded {}", event)
                }
            }

            override fun onEntryRemovedEvent(entryRemoveEvent: EntryRemovedEvent<CircuitBreaker>) {
                log.info("RegistryEventConsumer.onEntryRemovedEvent")
            }

            override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<CircuitBreaker>) {
                log.info("RegistryEventConsumer.onEntryReplacedEvent")
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<CircuitBreakerEventDemoApplication>(*args)
}
