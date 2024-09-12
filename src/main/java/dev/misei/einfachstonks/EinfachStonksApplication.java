package dev.misei.einfachstonks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EinfachStonksApplication {

    public static void main(String[] args) {
        SpringApplication.run(EinfachStonksApplication.class, args);
    }

}
