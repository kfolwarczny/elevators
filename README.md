# Elevator sim app
This is a simple spring boot app written in Kotlin.

## Building and testing

To execute build and test process execute following:
``./gradlew clean test``

## Running
To run the app please execute following:
``./gradlew bootRun``

## Playing

To play a bit with the app 3 endpoints where prepared:

``curl --request GET --url http://localhost:8080/api/elevator`` 
will list all of the available elevators

``
curl --request PUT --url http://localhost:8080/api/elevator/${ELEVATOR_ID}/cancel
``
will cancel the request for the given elevator by ID

````
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"toFloor":${FLOOR_NUMBER}}' \                      
  http://localhost:8080/api/elevator/request

````
  
Will request the elevator to given floor



