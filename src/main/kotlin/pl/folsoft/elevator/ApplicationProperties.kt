package pl.folsoft.elevator

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application")
class ApplicationProperties(var numberOfFloors: Int = 5,
                            var numberOfGroundFloors: Int = 0,
                            var numberOfElevators: Int = 3,
                            var floorPassingTime: Long = 10000)