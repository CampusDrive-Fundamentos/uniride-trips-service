package com.uniride.uniridetripsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UnirideTripsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnirideTripsServiceApplication.class, args);
    }

}