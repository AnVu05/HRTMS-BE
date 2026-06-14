package com.swp.hrtms.hrtmsbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HrtmsBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrtmsBeApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner mockData(
            com.swp.hrtms.hrtmsbe.repository.AdminRepository adminRepository,
            com.swp.hrtms.hrtmsbe.service.TournamentService tournamentService,
            com.swp.hrtms.hrtmsbe.service.RaceService raceService) {
        return args -> {
            if (adminRepository.count() == 0) {
                com.swp.hrtms.hrtmsbe.entity.Admin admin = new com.swp.hrtms.hrtmsbe.entity.Admin();
                admin.setUsername("admin_mock");
                admin.setPassword("123456");
                admin.setEmail("admin@mock.com");
                admin.setRole("ADMIN");
                admin.setEmployeeCode("EMP001");
                admin = adminRepository.save(admin);
                System.out.println("Mock Admin created with ID: " + admin.getId());

                com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest tournamentRequest = com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest.builder()
                        .adminId(admin.getId())
                        .name("Mock Spring Tournament 2026")
                        .startDate(java.time.LocalDate.now().plusDays(10))
                        .endDate(java.time.LocalDate.now().plusDays(20))
                        .allowedBreed("Thoroughbred")
                        .allowedHorseAge(4)
                        .status("PUBLIC")
                        .build();

                com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse tournamentResponse = tournamentService.createTournament(tournamentRequest);
                System.out.println("1. Mock Tournament created with ID: " + tournamentResponse.getId());

                com.swp.hrtms.hrtmsbe.dto.request.RaceCreateRequest race1 = com.swp.hrtms.hrtmsbe.dto.request.RaceCreateRequest.builder()
                        .name("Spring Cup - Race 1")
                        .date(java.time.LocalDate.now().plusDays(11))
                        .startTime(java.time.LocalTime.of(8, 0))
                        .endTime(java.time.LocalTime.of(10, 0))
                        .laps(5)
                        .numHorse(8)
                        .build();

                com.swp.hrtms.hrtmsbe.dto.request.RaceCreateRequest race2 = com.swp.hrtms.hrtmsbe.dto.request.RaceCreateRequest.builder()
                        .name("Spring Cup - Race 2")
                        .date(java.time.LocalDate.now().plusDays(11))
                        .startTime(java.time.LocalTime.of(10, 30))
                        .endTime(java.time.LocalTime.of(12, 0))
                        .laps(3)
                        .numHorse(6)
                        .build();

                com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest raceBatchRequest = com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest.builder()
                        .tournamentId(tournamentResponse.getId())
                        .races(java.util.List.of(race1, race2))
                        .build();

                raceService.createRacesBatch(raceBatchRequest);
                System.out.println("2. Mock Races created successfully for Tournament ID: " + tournamentResponse.getId());
                
                com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse details = raceService.getRaceDetailsByTournament(tournamentResponse.getId());
                System.out.println("3. Race Details API Output: " + details);
            }
        };
    }
}
