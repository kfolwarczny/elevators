package pl.folsoft.elevator.model

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.GlobalScope
import pl.folsoft.elevator.ApplicationProperties

internal class LiftFactoryTest : StringSpec() {
    init {

        val factory = LiftFactory(ApplicationProperties())

        "creation" should {
            "create list of lifts"{
                val lifts = factory.createLifts(3, GlobalScope)

                lifts.size shouldBe 3
                lifts.map { it.getId() } shouldContainExactly listOf(1, 2, 3)
            }
            "for zero return empty list" {
                val lifts = factory.createLifts(0, GlobalScope)

                lifts.shouldBeEmpty()
            }
            "for minus value return empty list" {
                factory.createLifts(-1, GlobalScope).shouldBeEmpty()
            }
        }

    }
}