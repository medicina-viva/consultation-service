package com.medicinaviva.consultation.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class AuthClient {
    private WebClient webClientBuilder;

    public AuthClient() {
        this.webClientBuilder = WebClient
                .builder()
                .baseUrl("http://authentication-service/")
                .build();
    }
}
