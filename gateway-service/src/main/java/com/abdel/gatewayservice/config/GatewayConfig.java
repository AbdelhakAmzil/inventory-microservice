package com.abdel.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("countries", r -> r.path("/restcountries/**")
                        .filters(f -> f
                                .rewritePath("/restcountries/(?<segment>.*)", "/v3.1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("countriesCB")
                                        .setFallbackUri("forward:/fallback/countries")
                                )
                        )
                        .uri("https://restcountries.com")
                )
                .route("muslimsalat", r -> r.path("/muslimsalat/**")
                        .filters(f -> f
                                .addRequestHeader("x-rapidapi-host", "muslimsalat.p.rapidapi.com")
                                .addRequestHeader("x-rapidapi-key", "fe5e774996msh4eb6e863d457420p1d2ffbjsnee0617ac5078")
                                .rewritePath("/muslimsalat/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("muslimsalatCB")
                                        .setFallbackUri("forward:/fallback/muslimsalat")
                                )
                        )
                        .uri("https://muslimsalat.p.rapidapi.com")
                )
                .route("customer-service", r -> r.path("/customers/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("customerCB")
                                        .setFallbackUri("forward:/fallback/customers")
                                )
                        )
                        .uri("lb://CUSTOMER-SERVICE")
                )
                .route("inventory-service", r -> r.path("/products/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("inventoryCB")
                                        .setFallbackUri("forward:/fallback/products")
                                )
                        )
                        .uri("lb://INVENTORY-SERVICE")
                )
                .route("billing-service", r -> r.path("/bills/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("billingCB")
                                        .setFallbackUri("forward:/fallback/bills")
                                )
                        )
                        .uri("lb://BILLING-SERVICE")
                )
                .build();
    }
}