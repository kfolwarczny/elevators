package pl.folsoft.elevator.model

import io.kotlintest.data.suspend.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.row

internal class LiftTest : StringSpec() {

    init {

        "calculating floor distance" {
            forall(
                row(1, 2, 1),
                row(2, 1, 1),
                row(-1, -2, 1),
                row(-2, -1, 1),
                row(-1, 2, 3),
                row(2, -1, 3),
                row(1, 1, 0),
                row(0, 0, 0),
                row(-1, -1, 0)
            ) { currentFloor, addressFloor, expectedDistance ->
                currentFloor.distance(addressFloor) shouldBe expectedDistance
            }
        }
    }
}
