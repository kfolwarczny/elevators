package pl.folsoft.elevator.model

import kotlinx.coroutines.CoroutineScope
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import pl.folsoft.elevator.ApplicationProperties

@EnableConfigurationProperties(ApplicationProperties::class)
@Service
class LiftFactory(private val applicationProperties: ApplicationProperties) {

    fun createLifts(numberOfLiftsToCreate: Int, scope: CoroutineScope): List<Lift> {
        return (1..numberOfLiftsToCreate).fold(emptyList<Lift>()) { acc, next ->
            acc + Lift(next, applicationProperties.floorPassingTime, scope)
        }
    }

}