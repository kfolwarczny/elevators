package pl.folsoft.elevator

import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.list.foldable.find
import arrow.core.none
import arrow.core.orElse
import arrow.core.some
import arrow.core.toOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import pl.folsoft.elevator.api.Elevator
import pl.folsoft.elevator.api.ElevatorController
import pl.folsoft.elevator.model.Lift
import pl.folsoft.elevator.model.LiftFactory
import pl.folsoft.elevator.model.distance

@EnableConfigurationProperties(ApplicationProperties::class)
@Service
class ElevatorManager(private val applicationProperties: ApplicationProperties,
                      factory: LiftFactory) : ElevatorController {

    private val supervisor = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + supervisor)

    val lifts: List<Lift> = factory.createLifts(applicationProperties.numberOfElevators, scope)

    override fun requestElevator(toFloor: Int): Option<Elevator> {
        LOGGER.info("Requesting elevator to $toFloor floor.")

        val floorRange = (applicationProperties.numberOfGroundFloors * -1)..applicationProperties.numberOfFloors

        return if (toFloor !in floorRange) {
            none()
        } else {
            when {
                lifts.allOnSameFloor() -> lifts.first().some()
                lifts.all { it.currentFloor() > toFloor } -> requestLiftFromFloor(Elevator.Direction.DOWN) { it.currentFloor() - toFloor }
                lifts.all { it.currentFloor() < toFloor } -> requestLiftFromFloor(Elevator.Direction.UP) { toFloor - it.currentFloor() }
                else -> lifts.giveClosestLift(toFloor)
            }
        }
            .peek { it.moveElevator(toFloor) }
    }

    private fun requestLiftFromFloor(direction: Elevator.Direction, minFun: (Lift) -> Int) =
        if (lifts.all { it.getDirection() == direction.getOpposite() }) {
            none()
        } else {
            lifts
                .find { it.isBusy() || it.getDirection() == direction }
                .orElse {
                    lifts.filter { !it.isBusy() }
                        .minBy(minFun)
                        .toOption()
                }
        }

    private fun List<Lift>.allOnSameFloor(): Boolean {
        val currentFloor = this.first().currentFloor()

        return this.fold(true) { acc, next ->
            if (!acc) {
                acc
            } else {
                currentFloor == next.currentFloor()
            }
        }
    }

    override fun getElevators() = lifts.toList()

    override fun releaseElevator(elevator: Elevator) {
        elevator.cancelRequest()
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(ElevatorManager::class.java)
    }
}

private fun List<Lift>.giveClosestLift(toFloor: Int) = this
    .filterNot { it.isBusy() }
    .map { it.currentFloor().distance(toFloor) to it }
    .minBy { it.first }
    .toOption()
    .map { it.second }


private fun <A> Option<A>.peek(f: (A) -> Unit): Option<A> {
    return when (this) {
        is Some -> {
            f(this.t)
            this
        }
        else -> this
    }
}
