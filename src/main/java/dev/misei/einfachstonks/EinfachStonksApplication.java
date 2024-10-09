package dev.misei.einfachstonks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableMongoRepositories
public class EinfachStonksApplication {

    public static void main(String[] args) {
        SpringApplication.run(EinfachStonksApplication.class, args);
    }

}
