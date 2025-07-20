package com.example.secondservice

import lombok.extern.slf4j.Slf4j
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/second-service")
@Slf4j
class SecondServiceController {

    private val log = logger()

    @GetMapping("/welcome")
    fun welcome(): String {
        return "Welcome to the Second service."
    }

    @GetMapping("/message")
    fun message(@RequestHeader("s-request") header: String): String {
        log.info(header)
        return "Hello World in Second Service."
    }

    @GetMapping("/check")
    fun check(): String {
        return "Hi, there. This is a message from Second Service."
    }
}