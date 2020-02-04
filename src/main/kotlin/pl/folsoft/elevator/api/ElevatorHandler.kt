package pl.folsoft.elevator.api

import arrow.core.Either
import arrow.core.extensions.list.foldable.find
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.folsoft.elevator.ElevatorManager


//TODO should be better tested
@Service
class ElevatorHandler(private val elevatorManager: ElevatorManager) {
    fun getAllLifts(): List<Elevator> = elevatorManager.getElevators()

    fun requestLift(request: LiftRequest): Either<String, Elevator> {
        LOGGER.info("Requesting lift to floor {}.", request.toFloor)

        return elevatorManager.requestElevator(request.toFloor)
                .toEither { "Could not provide lift that could fulfill this request" }
    }

    fun cancelRequest(liftId: Int): Either<String, Unit> {
        LOGGER.info("Trying to cancel request for lift {}.", liftId)

        return elevatorManager
                .lifts
                .find { it.getId() == liftId }
                .map { elevatorManager.releaseElevator(it) }
                .toEither { "Requested elevator does not exists" }
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(ElevatorHandler::class.java)
    }
}