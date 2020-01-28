package pl.folsoft.elevator.model

import kotlinx.coroutines.runBlocking
import pl.folsoft.elevator.api.Elevator

data class Lift(private val id: Int,
                private val floorPassingTime: Long,
                private val direction: Elevator.Direction = Elevator.Direction.NONE,
                private val addressFloor: Int = 0) : Elevator {

    override fun getDirection() = direction

    override fun getAddressedFloor() = addressFloor

    override fun getId() = id

    override fun moveElevator(toFloor: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isBusy(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun currentFloor(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}