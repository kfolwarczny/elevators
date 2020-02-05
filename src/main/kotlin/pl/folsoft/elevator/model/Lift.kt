package pl.folsoft.elevator.model

import arrow.core.toOption
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
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
                @JsonIgnore private val floorPassingTime: Long,
                @JsonIgnore private val scope: CoroutineScope = GlobalScope,
                private var direction: AtomicReference<Elevator.Direction> = AtomicReference(Elevator.Direction.NONE),
                private var currentFloor: AtomicInteger = AtomicInteger(0)) : Elevator {

    @JsonIgnore
    private var job: Job? = null
    private var addressFloor = AtomicInteger(0)

    override fun getDirection(): Elevator.Direction = direction.get()
    @JsonIgnore
    override fun getAddressedFloor() = addressFloor.get().toOption()

    override fun getId() = id

    override fun moveElevator(toFloor: Int) {
        if (job?.isActive == true) job?.cancel()

        val floorDistance = AtomicInteger(currentFloor.get().distance(toFloor))

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
            direction.set(Elevator.Direction.NONE)
        }
    }

    override fun isBusy(): Boolean = job?.isActive ?: false

    @JsonGetter
    fun addressFloor(): Int = addressFloor.get()

    @JsonGetter
    override fun currentFloor(): Int = currentFloor.get()

    override fun cancelRequest(): Unit? = job?.cancel()
}

fun Int.distance(toNumber: Int): Int {
    val currentValue = this

    val biggerNum = max(currentValue.absoluteValue, toNumber.absoluteValue)
    val smallerNum = min(currentValue.absoluteValue, toNumber.absoluteValue)

    return when {
        currentValue > 0 && toNumber > 0 -> biggerNum - smallerNum
        currentValue < 0 && toNumber < 0 -> biggerNum - smallerNum
        else -> currentValue.absoluteValue + toNumber.absoluteValue
    }
}
