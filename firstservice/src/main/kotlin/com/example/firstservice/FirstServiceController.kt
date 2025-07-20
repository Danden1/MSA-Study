package com.example.firstservice

import jakarta.servlet.http.HttpServletRequest
import lombok.extern.slf4j.Slf4j
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/first-service")
@Slf4j
class FirstServiceController(var env: Environment) {

    private val log = logger()
    @GetMapping("/welcome")
    fun welcome(): String {
        return "Welcome to the First service."
    }

    @GetMapping("/message")
    fun message(@RequestHeader("f-request") header: String): String {
        log.info(header)
        return "Hello World in First Service."
    }

    @GetMapping("/check")
    fun check(request: HttpServletRequest): String {
        val headers: Enumeration<String> = request.headerNames
        Collections.list(headers).stream().forEach { name: String ->
            val values: Enumeration<String> = request.getHeaders(name)
            Collections.list(values).stream().forEach { value: String -> println("$name=$value") }
        }

        log.info("Server port={}", request.serverPort)

        log.info(
            "spring.cloud.client.hostname={}",
            env.getProperty("spring.cloud.client.hostname")
        )
        log.info(
            "spring.cloud.client.ip-address={}",
            env.getProperty("spring.cloud.client.ip-address")
        )

        return String.format(
            "Hi, there. This is a message from First Service on PORT %s",
            env.getProperty("local.server.port")
        )
    }
}