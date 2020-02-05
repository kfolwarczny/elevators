package pl.folsoft.elevator.socket

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import pl.folsoft.elevator.api.Elevator
import pl.folsoft.elevator.api.ElevatorHandler
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class WebSocketHandler(private val producer: LiftsStatusProducer, private val objectMapper: ObjectMapper) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        LOGGER.info("Starting WS session with ID: {}", session.id)

        return session
                .send(producer.produce()
                        .map { objectMapper.writeValueAsString(it) }
                        .map { session.textMessage(it) })
                .doFinally {
                    LOGGER.info("Closing down the WS session with ID: {}", session.id)
                }
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)

    }
}

@Service
@EnableConfigurationProperties(SocketProperties::class)
class LiftsStatusProducer(private val elevatorHandler: ElevatorHandler,
                          private val properties: SocketProperties) {

    fun produce(): Flux<List<Elevator>> = Flux.interval(Duration.ofSeconds(5), Duration.ofMillis(properties.refresh))
            .map {
                LOGGER.info("Sending lifts status...")
                elevatorHandler.getAllLifts()
            }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(LiftsStatusProducer::class.java)
    }
}
