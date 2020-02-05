package pl.folsoft.elevator.socket

import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import pl.folsoft.elevator.api.ElevatorHandler
import pl.folsoft.elevator.model.Lift
import reactor.test.StepVerifier
import java.time.Duration

internal class LiftsStatusProducerTest : StringSpec() {

    private val mockScope = mockk<CoroutineScope>(relaxed = true)

    private val lifts = listOf(
        Lift(1, 10, mockScope),
        Lift(2, 10, mockScope),
        Lift(3, 10, mockScope)
    )
    private val handler = mockk<ElevatorHandler> {
        every { getAllLifts() } returns lifts
    }
    private val properties = mockk<SocketProperties> {
        every { refresh } returns 3000
    }

    private val producer = LiftsStatusProducer(handler, properties)

    init {
        "generates flux of lifts"{

            StepVerifier
                .withVirtualTime {
                    producer.produce().take(2)
                }
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(5))
                .expectNext(lifts)
                .thenAwait(Duration.ofMillis(3000))
                .expectNext(lifts)
                .verifyComplete()
        }
    }
}
