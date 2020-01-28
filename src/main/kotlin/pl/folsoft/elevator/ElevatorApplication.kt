package pl.folsoft.elevator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ElevatorApplication

fun main(args: Array<String>) {
	runApplication<ElevatorApplication>(*args)
}
