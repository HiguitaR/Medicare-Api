package com.higuitar.medicare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class MedicareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicareApplication.class, args);
    }

}
