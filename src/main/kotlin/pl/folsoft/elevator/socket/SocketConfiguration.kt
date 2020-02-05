package pl.folsoft.elevator.socket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
@EnableConfigurationProperties(SocketProperties::class)
class SocketConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper()
    }

    @Bean
    fun handlerMapping(wsh: WebSocketHandler, socketProperties: SocketProperties): HandlerMapping = object : SimpleUrlHandlerMapping() {
        init {
            urlMap = mapOf(socketProperties.mapping + "/**" to wsh)
            order = 10
        }
    }

    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter = WebSocketHandlerAdapter()
}


@ConfigurationProperties(prefix = "application.socket")
class SocketProperties(var mapping: String = "/ws/lifts",
                       var refresh: Long = 5000)
