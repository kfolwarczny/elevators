package pl.folsoft.elevator.api

import arrow.core.left
import arrow.core.right
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.GlobalScope
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import pl.folsoft.elevator.model.Lift

internal class ElevatorEndpointTest : StringSpec() {

    private val handler = mockk<ElevatorHandler>()

    private val client = WebTestClient.bindToRouterFunction(ElevatorEndpoint().route(handler)).build()

    init {

        "return list of lifts" {
            val expectedLifts = listOf(
                    Lift(1, 10, scope = GlobalScope),
                    Lift(2, 10, scope = GlobalScope),
                    Lift(3, 10, scope = GlobalScope)
            )

            every { handler.getAllLifts() } returns expectedLifts

            client.get()
                    .uri("/api/elevator")
                    .exchange()
                    .expectBodyList(Lift::class.java)
                    .hasSize(3)
        }
        "request POST" should {
            "return 404 when no available lifts" {
                every { handler.requestLift(any()) } returns "No lifts available for this request".left()

                client.post()
                        .uri("/api/elevator/request")
                        .bodyValue(LiftRequest(3))
                        .exchange()
                        .expectStatus()
                        .isNotFound
            }
            "returns 200 with id of lift that fulfill the request" {
                every { handler.requestLift(any()) } returns Lift(1, 10).right()

                client.post()
                        .uri("/api/elevator/request")
                        .bodyValue(LiftRequest(3))
                        .exchange()
                        .expectBody<LiftResponse>()
                        .consumeWith { it.responseBody?.liftId shouldBe 1 }
            }
        }

        "cancellation POST" should {
            "return 404 for unknown lift"{
                every { handler.cancelRequest(any()) } returns "Lift with ID does not exists".left()

                client.put()
                        .uri("/api/elevator/1/cancel")
                        .exchange()
                        .expectStatus()
                        .isNotFound
            }

            "return 204 when accepted" {
                every { handler.cancelRequest(any()) } returns Unit.right()

                client.put()
                        .uri("/api/elevator/1/cancel")
                        .exchange()
                        .expectStatus()
                        .isNoContent
            }
        }
    }
}