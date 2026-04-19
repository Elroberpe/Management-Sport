package com.sport.managementsport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ManagementSportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagementSportApplication.class, args);
    }

}
