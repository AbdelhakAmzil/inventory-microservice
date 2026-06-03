package com.abdel.gatewayservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/countries")
    public Mono<String> countriesFallback() {
        return Mono.just("Countries service is unavailable. Please try again later.");
    }

    @GetMapping("/muslimsalat")
    public Mono<String> muslimsalatFallback() {
        return Mono.just("Muslim Salat service is unavailable. Please try again later.");
    }

    @GetMapping("/customers")
    public Mono<String> customersFallback() {
        return Mono.just("Customer service is unavailable. Please try again later.");
    }

    @GetMapping("/products")
    public Mono<String> productsFallback() {
        return Mono.just("Inventory service is unavailable. Please try again later.");
    }

    @GetMapping("/bills")
    public Mono<String> billsFallback() {
        return Mono.just("Billing service is unavailable. Please try again later.");
    }
}