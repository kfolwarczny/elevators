package pl.folsoft.elevator

import io.kotlintest.assertions.arrow.option.shouldBeNone
import io.kotlintest.assertions.arrow.option.shouldBeSome
import io.kotlintest.data.suspend.forall
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.GlobalScope
import pl.folsoft.elevator.api.Elevator
import pl.folsoft.elevator.model.Lift
import pl.folsoft.elevator.model.LiftFactory
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

internal class ElevatorManagerTest : StringSpec() {

    private val properties = mockk<ApplicationProperties> {
        every { numberOfElevators } returns 3
        every { numberOfGroundFloors } returns 3
        every { numberOfFloors } returns 5
    }
    private val factory = mockk<LiftFactory>(relaxed = true)

    init {
        "requesting lift" should {

            "for floor out range return none" {
                forall(
                        row(10),
                        row(-4)
                ) { toFloor ->

                    val manger = ElevatorManager(properties, factory)

                    val lift = manger.requestElevator(toFloor)

                    lift.shouldBeNone()
                }
            }

            "for lifts moving in different direction return none" {
                forall(
                        row(
                                listOf(
                                        Lift(1, 10, currentFloor = AtomicInteger(3), direction = AtomicReference(Elevator.Direction.UP), scope = GlobalScope),
                                        Lift(2, 10, currentFloor = AtomicInteger(2), direction = AtomicReference(Elevator.Direction.UP), scope = GlobalScope),
                                        Lift(3, 10, currentFloor = AtomicInteger(4), direction = AtomicReference(Elevator.Direction.UP), scope = GlobalScope)
                                ),
                                1
                        ),
                        row(
                                listOf(
                                        Lift(1, 10, currentFloor = AtomicInteger(2), direction = AtomicReference(Elevator.Direction.DOWN), scope = GlobalScope),
                                        Lift(2, 10, currentFloor = AtomicInteger(1), direction = AtomicReference(Elevator.Direction.DOWN), scope = GlobalScope),
                                        Lift(3, 10, currentFloor = AtomicInteger(-1), direction = AtomicReference(Elevator.Direction.DOWN), scope = GlobalScope)
                                ),
                                3
                        )
                ) { lifts, toFloor ->
                    clearMocks(factory)
                    every { factory.createLifts(any(), any()) } returns lifts

                    val manger = ElevatorManager(properties, factory)

                    val lift = manger.requestElevator(toFloor)

                    lift.shouldBeNone()
                }
            }

            "returns the most fitting" {
                forall(
                        row(
                                listOf(
                                        Lift(1, 10, GlobalScope),
                                        Lift(2, 10, GlobalScope),
                                        Lift(3, 10, GlobalScope)
                                ),
                                2,
                                1
                        ),
                        row(
                                listOf(
                                        Lift(1, 10, currentFloor = AtomicInteger(3), scope = GlobalScope),
                                        Lift(2, 10, currentFloor = AtomicInteger(2), scope = GlobalScope),
                                        Lift(3, 10, currentFloor = AtomicInteger(4), scope = GlobalScope)
                                ),
                                1,
                                2
                        ),
                        row(
                                listOf(
                                        Lift(1, 10, currentFloor = AtomicInteger(-3), scope = GlobalScope),
                                        Lift(2, 10, currentFloor = AtomicInteger(-2), scope = GlobalScope),
                                        Lift(3, 10, currentFloor = AtomicInteger(-1), scope = GlobalScope)
                                ),
                                2,
                                3
                        )
                ) { lifts, toFloor, expectedLiftId ->
                    clearMocks(factory)
                    every { factory.createLifts(any(), any()) } returns lifts

                    val manger = ElevatorManager(properties, factory)

                    val lift = manger.requestElevator(toFloor)

                    lift.shouldBeSome {
                        it.getId() shouldBe expectedLiftId
                    }
                }
            }
        }
    }
}