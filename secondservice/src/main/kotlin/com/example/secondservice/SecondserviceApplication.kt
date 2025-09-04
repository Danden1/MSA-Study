package com.example.secondservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.net.InetAddress

@SpringBootApplication
class SecondserviceApplication

fun main(args: Array<String>) {
	runApplication<SecondserviceApplication>(*args)
	InetAddress.getLocalHost().hostName
}
