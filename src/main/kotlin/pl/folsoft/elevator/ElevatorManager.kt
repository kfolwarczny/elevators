package pl.folsoft.elevator

import arrow.core.Option
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
                else -> lifts.find { it.currentFloor() == toFloor }
            }
        }
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(ElevatorManager::class.java)

        const val GROUND_FLOOR = 0
    }
}