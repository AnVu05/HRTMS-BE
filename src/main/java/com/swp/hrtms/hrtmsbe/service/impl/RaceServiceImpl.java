package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.RaceCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResponse;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.service.RaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;
    private final TournamentRepository tournamentRepository;
    private final RefereeRepository refereeRepository;

    @Override
    @Transactional
    public List<RaceResponse> createRacesBatch(RaceBatchCreateRequest request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + request.getTournamentId()));

        List<Race> racesToSave = new ArrayList<>();

        for (int i = 0; i < request.getRaces().size(); i++) {
            RaceCreateRequest raceReq = request.getRaces().get(i);

            // 1. Validation: Overlap in DB (Tournament)
            if (raceRepository.existsOverlappingInTournament(tournament.getId(), raceReq.getDate(), raceReq.getStartTime(), raceReq.getEndTime())) {
                throw new IllegalArgumentException("Race '" + raceReq.getName() + "' overlaps with an existing race in the tournament.");
            }

            // 2. Validation: Overlap within the incoming list (Tournament)
            for (int j = 0; j < i; j++) {
                RaceCreateRequest previousReq = request.getRaces().get(j);
                if (raceReq.getDate().equals(previousReq.getDate()) && 
                    raceReq.getStartTime().isBefore(previousReq.getEndTime()) && 
                    raceReq.getEndTime().isAfter(previousReq.getStartTime())) {
                    throw new IllegalArgumentException("Race '" + raceReq.getName() + "' overlaps with another race in the same request payload.");
                }
            }

            Referee referee = null;
            if (raceReq.getRefereeId() != null) {
                referee = refereeRepository.findById(raceReq.getRefereeId())
                        .orElseThrow(() -> new IllegalArgumentException("Referee not found with id: " + raceReq.getRefereeId()));

                // 3. Validation: Overlap in DB (Referee)
                if (raceRepository.existsOverlappingForReferee(referee.getId(), raceReq.getDate(), raceReq.getStartTime(), raceReq.getEndTime())) {
                    throw new IllegalArgumentException("Referee is already assigned to another overlapping race.");
                }

                // 4. Validation: Overlap within the incoming list (Referee)
                for (int j = 0; j < i; j++) {
                    RaceCreateRequest previousReq = request.getRaces().get(j);
                    if (raceReq.getRefereeId().equals(previousReq.getRefereeId()) &&
                        raceReq.getDate().equals(previousReq.getDate()) && 
                        raceReq.getStartTime().isBefore(previousReq.getEndTime()) && 
                        raceReq.getEndTime().isAfter(previousReq.getStartTime())) {
                        throw new IllegalArgumentException("Referee is assigned to overlapping races in the same request payload.");
                    }
                }
            }

            Race race = Race.builder()
                    .tournament(tournament)
                    .name(raceReq.getName())
                    .date(raceReq.getDate())
                    .startTime(raceReq.getStartTime())
                    .endTime(raceReq.getEndTime())
                    .laps(raceReq.getLaps())
                    .numHorse(raceReq.getNumHorse())
                    .referee(referee)
                    .status("PENDING_REFEREE")
                    .build();
            
            racesToSave.add(race);
        }

        racesToSave = raceRepository.saveAll(racesToSave);

        List<RaceResponse> responses = new ArrayList<>();
        for (Race r : racesToSave) {
            responses.add(RaceResponse.builder()
                    .id(r.getId())
                    .tournamentId(r.getTournament().getId())
                    .name(r.getName())
                    .date(r.getDate())
                    .startTime(r.getStartTime())
                    .endTime(r.getEndTime())
                    .laps(r.getLaps())
                    .numHorse(r.getNumHorse())
                    .refereeId(r.getReferee() != null ? r.getReferee().getId() : null)
                    .status(r.getStatus())
                    .build());
        }

        return responses;
    }
}
