package example.apigatewayservice.filter

import example.apigatewayservice.logger
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class LoggingFilter : AbstractGatewayFilterFactory<LoggingFilter.Config>(
    Config::class.java
) {
    private val log = logger()

    class Config(val baseMessage : String,
        val preLogger: Boolean,
        val postLogger: Boolean) {

    }

//    override fun apply(config: Config): GatewayFilter {
//
//        return GatewayFilter { exchange, chain ->
//
//            val request = exchange.request
//            val response = exchange.response
//
//            log.info("Logging PRE Filter : baseMessage: {}, {}", config.baseMessage, request.remoteAddress)
//
//            if (config.preLogger) {
//                log.info("Logging Filter start: request uri -> {}", request.uri)
//            }
//
//            chain.filter(exchange).then(Mono.fromRunnable {
//                if (config.postLogger) {
//                    log.info("Logging Filter end: response code => {}", response.statusCode)
//                }
//            })
//        }
//    }

    override fun apply(config: Config): GatewayFilter {

        return OrderedGatewayFilter ({ exchange, chain ->

            val request = exchange.request
            val response = exchange.response

            log.info("Logging PRE Filter : baseMessage: {}, {}", config.baseMessage, request.remoteAddress)

            if (config.preLogger) {
                log.info("Logging Filter start: request uri -> {}", request.uri)
            }

            chain.filter(exchange).then(Mono.fromRunnable {
                if (config.postLogger) {
                    log.info("Logging Filter end: response code => {}", response.statusCode)
                }
            })
        }, Ordered.HIGHEST_PRECEDENCE)
    }
}