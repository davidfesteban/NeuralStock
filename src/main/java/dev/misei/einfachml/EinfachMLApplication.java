package dev.misei.einfachml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class EinfachMLApplication {

    public static void main(String[] args) {
        SpringApplication.run(EinfachMLApplication.class, args);
    }

}
