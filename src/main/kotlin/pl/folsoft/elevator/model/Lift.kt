package pl.folsoft.elevator.model

import arrow.core.toOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.folsoft.elevator.api.Elevator
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

data class Lift(private val id: Int,
                private val floorPassingTime: Long,
                private val scope: CoroutineScope,
                private var direction: AtomicReference<Elevator.Direction> = AtomicReference(Elevator.Direction.NONE),
                private var currentFloor: AtomicInteger = AtomicInteger(0)) : Elevator {

    private var job: Job? = null
    private var addressFloor = AtomicInteger()

    override fun getDirection(): Elevator.Direction = direction.get()

    override fun getAddressedFloor() = addressFloor.get().toOption()

    override fun getId() = id

    override fun moveElevator(toFloor: Int) {
        val floorDistance = AtomicInteger(currentFloor.distance(toFloor))

        direction.set(when {
            toFloor > currentFloor.get() -> Elevator.Direction.UP
            toFloor < currentFloor.get() -> Elevator.Direction.DOWN
            else -> Elevator.Direction.NONE
        })

        job = scope.launch {
            addressFloor.set(toFloor)
            while (floorDistance.getAndDecrement() > 0) {
                delay(floorPassingTime)
                when {
                    direction.get() == Elevator.Direction.UP -> currentFloor.incrementAndGet()
                    direction.get() == Elevator.Direction.DOWN -> currentFloor.decrementAndGet()
                }
            }
        }
    }

    override fun isBusy(): Boolean = job?.isActive ?: false

    override fun currentFloor(): Int = currentFloor.get()
}

fun AtomicInteger.distance(toNumber: Int): Int {
    val currentValue = this.get()

    val biggerNum = max(currentValue.absoluteValue, toNumber.absoluteValue)
    val smallerNum = min(currentValue.absoluteValue, toNumber.absoluteValue)

    return when {
        currentValue > 0 && toNumber > 0 -> biggerNum - smallerNum
        currentValue < 0 && toNumber < 0 -> biggerNum - smallerNum
        else -> currentValue.absoluteValue + toNumber.absoluteValue
    }
}