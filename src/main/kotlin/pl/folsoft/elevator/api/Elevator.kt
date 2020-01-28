package pl.folsoft.elevator.api

interface Elevator {

    /**
     * Enumeration for describing elevator's direction.
     */
    enum class Direction {
        UP, DOWN, NONE
    }

    /**
     * Tells which direction is the elevator going in.
     *
     * @return Direction Enumeration value describing the direction.
     */
    fun getDirection(): Direction

    /**integer
     * If the elevator is moving. This is the target floor.
     *
     * @return primitive integer number of floor
     */
    fun getAddressedFloor(): Int

    /**
     * Get the Id of this elevator.
     *
     * @return primitive integer representing the elevator.
     */
    fun getId(): Int

    /**
     * Command to move the elevator to the given floor.
     *
     * @param toFloor int where to go.
     */
    fun moveElevator(toFloor: Int)

    /**
     * Check if the elevator is occupied at the moment.
     *
     * @return true if busy.
     */
    fun isBusy(): Boolean

    /**
     * Reports which floor the elevator is at right now.
     *
     * @return int actual floor at the moment.
     */
    fun currentFloor(): Int
}