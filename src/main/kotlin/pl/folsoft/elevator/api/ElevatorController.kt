package pl.folsoft.elevator.api

import arrow.core.Option

interface ElevatorController {

    /**
     * Request an elevator to the specified floor.
     *
     * @param toFloor addressed floor as integer.
     * @return The Elevator that is going to the floor, if there is one to move.
     */
    fun requestElevator(toFloor: Int): Option<Elevator> // I'd changed it to Option<T> as if the requested floor is out of order we should handle it and I don't want to throw exception

    /**
     * A snapshot list of all elevators in the system.
     *
     * @return A List with all [Elevator] objects.
     */
    fun getElevators(): List<Elevator>

    /**
     * Telling the controller that the given elevator is free for new
     * operations.
     *
     * @param elevator the elevator that shall be released.
     */
    fun releaseElevator(elevator: Elevator)
}