package example.apigatewayservice.filter

import example.apigatewayservice.logger
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CustomFilter : AbstractGatewayFilterFactory<CustomFilter.Config>(
    Config::class.java
) {
    private val log = logger()

    class Config {

    }

    override fun apply(config: Config?): GatewayFilter {

        return GatewayFilter { exchange, chain ->

            val request = exchange.request
            val response = exchange.response

            log.info("CustomFilter PRE Filter : request id -> {}", request.id)

            chain.filter(exchange).then(Mono.fromRunnable {
                log.info("Custom Post Filter : response code => {}", response.statusCode)
            })
        }
    }
}