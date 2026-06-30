package com.swp.hrtms.hrtmsbe;

import com.swp.hrtms.hrtmsbe.mock.MockData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HrtmsBeApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(HrtmsBeApplication.class, args);
        MockData mockData = context.getBean(MockData.class);
        mockData.init();

    }
}
