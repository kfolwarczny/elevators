package pl.folsoft.elevator.model

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.should
import io.kotlintest.specs.StringSpec
import pl.folsoft.elevator.ApplicationProperties

internal class LiftFactoryTest : StringSpec() {
    init {

        val factory = LiftFactory(ApplicationProperties())

        "creation" should {
            "create list of lifts"{
                val lifts = factory.createLifts(3)

                lifts shouldContainExactly listOf(Lift(1, 10000), Lift(2, 10000), Lift(3, 10000))
            }
            "for zero return empty list" {
                val lifts = factory.createLifts(0)

                lifts.shouldBeEmpty()
            }
            "for minus value return empty list" {
                factory.createLifts(-1).shouldBeEmpty()
            }
        }

    }
}