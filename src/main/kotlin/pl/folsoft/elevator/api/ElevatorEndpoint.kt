package pl.folsoft.elevator.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import pl.folsoft.elevator.api.ElevatorEndpoint.Companion.LIFT_ID_PARAM
import reactor.core.publisher.Mono

@Configuration
class ElevatorEndpoint {

    //TODO should have some openAPI but not yet available for functional endpoints
    @Bean
    fun route(elevatorHandler: ElevatorHandler) = router {
        GET("/api/elevator") {
            elevatorHandler.getAllLifts().serverResponse()
        }
        POST("/api/elevator/request") { req ->
            req.bodyToMono(LiftRequest::class.java)
                    .flatMap {
                        elevatorHandler.requestLift(it)
                                .fold(
                                        { notFound().build() },
                                        { elevator ->
                                            ok()
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .bodyValue(LiftResponse(elevator.getId()))
                                        }
                                )
                    }
        }
        PUT("/api/elevator/{$LIFT_ID_PARAM}/cancel") {
            elevatorHandler.cancelRequest(it.extractLiftId())
                    .fold({ notFound().build() }, { noContent().build() })
        }
    }

    companion object {
        const val LIFT_ID_PARAM = "liftId"
    }
}

//TODO should be secured for values not INT
private fun ServerRequest.extractLiftId(): Int = this.pathVariable(LIFT_ID_PARAM).toInt()

private fun <E> List<E>.serverResponse(): Mono<ServerResponse> {
    return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(this)
}

data class LiftResponse(val liftId: Int)
data class LiftRequest(val toFloor: Int)