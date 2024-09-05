package com.medicinaviva.consultationmanagerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class ConsultationManagerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultationManagerServiceApplication.class, args);
    }
}
