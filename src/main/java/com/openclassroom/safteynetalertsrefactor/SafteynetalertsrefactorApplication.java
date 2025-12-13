package com.openclassroom.safteynetalertsrefactor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SafteynetalertsrefactorApplication {

    // Main method to start the Spring Boot application
    public static void main(String[] args) {
        log.info("Starting SafteynetalertsrefactorApplication with args: {}", (Object) args);
        try {
            SpringApplication.run(SafteynetalertsrefactorApplication.class, args);
            log.info("SafteynetalertsrefactorApplication started successfully.");
        } catch (Throwable t) {
            log.error("SafteynetalertsrefactorApplication failed to start.", t);
            throw t;
        }
    }

}