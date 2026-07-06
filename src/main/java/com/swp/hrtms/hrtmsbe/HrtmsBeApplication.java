package com.swp.hrtms.hrtmsbe;

import com.swp.hrtms.hrtmsbe.mock.MockData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class HrtmsBeApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(HrtmsBeApplication.class, args);
        MockData mockData = context.getBean(MockData.class);
        mockData.init();

    }
}
