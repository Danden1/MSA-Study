package com.example.secondservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SecondserviceApplication

fun main(args: Array<String>) {
	runApplication<SecondserviceApplication>(*args)
}
