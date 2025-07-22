package example.apigatewayservice.filter

import example.apigatewayservice.logger
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GlobalFilter : AbstractGatewayFilterFactory<GlobalFilter.Config>(
    Config::class.java
) {
    private val log = logger()

    class Config(val baseMessage : String,
        val preLogger: Boolean,
        val postLogger: Boolean) {

    }

    override fun apply(config: Config): GatewayFilter {

        return GatewayFilter { exchange, chain ->

            val request = exchange.request
            val response = exchange.response

            log.info("Global PRE Filter : baseMessage: {}, {}", config.baseMessage, request.remoteAddress)

            if (config.preLogger) {
                log.info("Global Filter start: request id -> {}", request.id)
            }

            chain.filter(exchange).then(Mono.fromRunnable {
                if (config.postLogger) {
                    log.info("Global Filter end: response code => {}", response.statusCode)
                }
            })
        }
    }
}