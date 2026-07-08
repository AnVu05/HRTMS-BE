package com.swp.hrtms.hrtmsbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HrtmsBeApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(com.swp.hrtms.hrtmsbe.HrtmsBeApplication.class, args);

    }
}