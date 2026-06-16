package com.swp.hrtms.hrtmsbe.mock;

import com.swp.hrtms.hrtmsbe.entity.*;
import com.swp.hrtms.hrtmsbe.repository.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class MockData {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final RefereeRepository refereeRepository;
    private final HorseOwnerRepository horseOwnerRepository;
    private final HorseRepository horseRepository;
    private final TournamentRepository tournamentRepository;
    private final RaceRepository raceRepository;

    public MockData(UserRepository userRepository,
                    AdminRepository adminRepository,
                    RefereeRepository refereeRepository,
                    HorseOwnerRepository horseOwnerRepository,
                    HorseRepository horseRepository,
                    TournamentRepository tournamentRepository,
                    RaceRepository raceRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.refereeRepository = refereeRepository;
        this.horseOwnerRepository = horseOwnerRepository;
        this.horseRepository = horseRepository;
        this.tournamentRepository = tournamentRepository;
        this.raceRepository = raceRepository;
    }

    public void generateData() {
        // Check if data already exists to avoid duplicate data on application restart
        if (userRepository.count() > 0) {
            System.out.println("Data already exists. Skipping test data generation.");
            return;
        }

        System.out.println("Generating test data...");

        // 1. Create Admin
        Admin admin = new Admin();
        admin.setUsername("admin1");
        admin.setPassword("password123");
        admin.setEmail("admin1@test.com");
        admin.setRole(UserRole.ADMIN.name());
        admin.setEmployeeCode("EMP-ADMIN-001");
        adminRepository.save(admin);

        // 2. Create Referee
        Referee referee = new Referee();
        referee.setUsername("referee1");
        referee.setPassword("password123");
        referee.setEmail("referee1@test.com");
        referee.setRole(UserRole.REFEREE.name());
        refereeRepository.save(referee);

        // 3. Create HorseOwner Users and HorseOwners
        User ownerUser1 = new User();
        ownerUser1.setUsername("owner1");
        ownerUser1.setPassword("password123");
        ownerUser1.setEmail("owner1@test.com");
        ownerUser1.setRole(UserRole.HORSE_OWNER.name());
        userRepository.save(ownerUser1);

        HorseOwner owner1 = new HorseOwner();
        owner1.setUser(ownerUser1);
        owner1.setOwnerName("John Doe");
        owner1.setPhone("1234567890");
        horseOwnerRepository.save(owner1);

        User ownerUser2 = new User();
        ownerUser2.setUsername("owner2");
        ownerUser2.setPassword("password123");
        ownerUser2.setEmail("owner2@test.com");
        ownerUser2.setRole(UserRole.HORSE_OWNER.name());
        userRepository.save(ownerUser2);

        HorseOwner owner2 = new HorseOwner();
        owner2.setUser(ownerUser2);
        owner2.setOwnerName("Jane Smith");
        owner2.setPhone("0987654321");
        horseOwnerRepository.save(owner2);

        // 4. Create Horses
        Horse horse1 = new Horse();
        horse1.setOwner(owner1);
        horse1.setName("Thunder");
        horse1.setAge(5);
        horse1.setBreed("Thoroughbred");
        horse1.setStatus(HorseStatus.ACTIVE);
        horseRepository.save(horse1);

        Horse horse2 = new Horse();
        horse2.setOwner(owner1);
        horse2.setName("Lightning");
        horse2.setAge(4);
        horse2.setBreed("Arabian");
        horse2.setStatus(HorseStatus.ACTIVE);
        horseRepository.save(horse2);

        Horse horse3 = new Horse();
        horse3.setOwner(owner2);
        horse3.setName("Storm");
        horse3.setAge(6);
        horse3.setBreed("Thoroughbred");
        horse3.setStatus(HorseStatus.INJURED);
        horseRepository.save(horse3);

        // 5. Create Tournaments
        Tournament tournament1 = new Tournament();
        tournament1.setAdmin(admin);
        tournament1.setName("Spring Cup 2026");
        tournament1.setStartDate(LocalDate.now().plusDays(10));
        tournament1.setEndDate(LocalDate.now().plusDays(12));
        tournament1.setAllowedBreed("Thoroughbred");
        tournament1.setAllowedHorseAge(4);
        tournament1.setStatus("UPCOMING");
        tournamentRepository.save(tournament1);

        Tournament tournament2 = new Tournament();
        tournament2.setAdmin(admin);
        tournament2.setName("Summer Championship");
        tournament2.setStartDate(LocalDate.now().plusMonths(2));
        tournament2.setEndDate(LocalDate.now().plusMonths(2).plusDays(5));
        tournament2.setAllowedBreed("Arabian");
        tournament2.setAllowedHorseAge(5);
        tournament2.setStatus("UPCOMING");
        tournamentRepository.save(tournament2);

        // 6. Create Races
        Race race1 = new Race();
        race1.setTournament(tournament1);
        race1.setName("Race 1 - Qualifiers");
        race1.setDate(LocalDate.now().plusDays(10));
        race1.setStartTime(LocalTime.of(10, 0));
        race1.setEndTime(LocalTime.of(10, 30));
        race1.setLaps(5);
        race1.setNumHorse(8);
        race1.setReferee(referee);
        race1.setStatus("SCHEDULED");
        raceRepository.save(race1);

        Race race2 = new Race();
        race2.setTournament(tournament1);
        race2.setName("Race 2 - Finals");
        race2.setDate(LocalDate.now().plusDays(12));
        race2.setStartTime(LocalTime.of(15, 0));
        race2.setEndTime(LocalTime.of(15, 30));
        race2.setLaps(10);
        race2.setNumHorse(8);
        race2.setReferee(referee);
        race2.setStatus("SCHEDULED");
        raceRepository.save(race2);

        System.out.println("Test data generated successfully!");
    }
}
