package com.swp.hrtms.hrtmsbe;

import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.RaceCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse;
import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.service.RaceService;
import com.swp.hrtms.hrtmsbe.service.TournamentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootApplication
public class HrtmsBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrtmsBeApplication.class, args);
    }

}
