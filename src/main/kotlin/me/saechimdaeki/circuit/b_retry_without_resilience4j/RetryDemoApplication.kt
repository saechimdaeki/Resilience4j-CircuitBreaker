package me.saechimdaeki.circuit.b_retry_without_resilience4j

import io.github.resilience4j.core.registry.EntryAddedEvent
import io.github.resilience4j.core.registry.EntryRemovedEvent
import io.github.resilience4j.core.registry.EntryReplacedEvent
import io.github.resilience4j.core.registry.RegistryEventConsumer
import io.github.resilience4j.retry.Retry
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class RetryDemoApplication {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun myRegistryEventConsumer(): RegistryEventConsumer<Retry> {
        return object : RegistryEventConsumer<Retry> {
            override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<Retry>) {
                log.info("RegistryEventConsumer.onEntryAddedEvent")
                entryAddedEvent.addedEntry.eventPublisher.onEvent { event ->
                    log.info(event.toString())
                }
            }

            override fun onEntryRemovedEvent(entryRemoveEvent: EntryRemovedEvent<Retry>) {
                log.info("RegistryEventConsumer.onEntryRemovedEvent")
            }

            override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<Retry>) {
                log.info("RegistryEventConsumer.onEntryReplacedEvent")
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<RetryDemoApplication>(*args)
}
