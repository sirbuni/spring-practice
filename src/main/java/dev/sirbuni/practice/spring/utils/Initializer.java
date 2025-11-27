package dev.sirbuni.practice.spring.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by: Elias Yohana
 */
@Component
public class Initializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(Initializer.class);
    @Override
    public void run(ApplicationArguments args) throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");

        log.info("{} APPLICATION INITIALIZING STARTED [{}] {}",
                "=".repeat(20), LocalDateTime.now().format(dtf), "=".repeat(20));
        Thread.sleep(Duration.ofSeconds(2));
        log.info("{} APPLICATION INITIALIZING COMPLETED [{}] {}",
                "=".repeat(20), LocalDateTime.now().format(dtf), "=".repeat(20));


    }
}
