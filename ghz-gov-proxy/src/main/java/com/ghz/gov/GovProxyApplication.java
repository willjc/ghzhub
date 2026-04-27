package com.ghz.gov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GovProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovProxyApplication.class, args);
    }
}
