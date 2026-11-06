package com.nhom8.pilates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PilatesApplication {
    public static void main(String[] args) {
        SpringApplication.run(PilatesApplication.class, args);
    }
}
