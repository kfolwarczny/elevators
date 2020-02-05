package pl.folsoft.elevator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
class ElevatorApplication

fun main(args: Array<String>) {
	runApplication<ElevatorApplication>(*args)
}
