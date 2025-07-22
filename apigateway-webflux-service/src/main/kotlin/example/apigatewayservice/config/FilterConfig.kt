//package example.apigatewayservice.config
//
//import org.springframework.cloud.gateway.route.RouteLocator
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
//import org.springframework.cloud.gateway.route.builder.filters
//import org.springframework.cloud.gateway.route.builder.routes
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//
//@Configuration
//class FilterConfig {
//
//
//    @Bean
//    fun getRouterLocator(builder: RouteLocatorBuilder) : RouteLocator {
//        return builder.routes{
//            route {
//                path("/first-service/**")
//                filters {
//                    // 강제로 헤더 정보를 넣어줌.
//                    addRequestHeader("f-request", "1st-request-header-by-java")
//                    addResponseHeader("f-response", "1st-response-header-by-java")
//                }
//                uri("http://localhost:8081")
//            }
//
//
//            route {
//                path("/second-service/**")
//                filters {
//                    // 강제로 헤더 정보를 넣어줌.
//                    addRequestHeader("s-request", "2nd-request-header-by-java")
//                    addResponseHeader("s-response", "2nd-response-header-by-java")
//                }
//                uri("http://localhost:8082") ///second-service/message
//            }
//        }
//    }
//}