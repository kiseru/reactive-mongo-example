package com.example.mongoexample

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import reactor.core.publisher.Mono

@SpringBootApplication
@EnableReactiveMongoRepositories
class MongoExampleApplication

fun main(args: Array<String>) {
    val ctx = runApplication<MongoExampleApplication>(*args)
    val repository = ctx.getBean(VehicleRepository::class.java)
    runBlocking {
        println("Number of Vehicles: ${repository.count().awaitSingle()}")
        repository.save(Vehicle("TEM0001", "RED", 4, 4)).awaitSingle()
        repository.save(Vehicle("TEM0002", "RED", 4, 4)).awaitSingle()
        println("Number of Vehicles: ${repository.count().awaitSingle()}")
        val vehicle = repository.findByVehicleNo("TEM0001").awaitSingle()
        println(vehicle)
        repository.deleteAll().awaitSingleOrNull()
        println("Number of Vehicles: ${repository.count().awaitSingle()}")
    }
    ctx.close()
}

interface VehicleRepository : ReactiveMongoRepository<Vehicle, String> {

    fun findByVehicleNo(vehicleNo: String): Mono<Vehicle>
}

@Document(collection = "vehicles")
data class Vehicle(
    @Id
    var vehicleNo: String,
    var color: String,
    var wheel: Int,
    var seat: Int,
)