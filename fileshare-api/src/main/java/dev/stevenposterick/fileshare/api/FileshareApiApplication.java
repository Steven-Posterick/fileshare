package dev.stevenposterick.fileshare.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FileshareApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileshareApiApplication.class, args);
    }

}
