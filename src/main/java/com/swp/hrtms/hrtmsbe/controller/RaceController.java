package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResponse;
import com.swp.hrtms.hrtmsbe.service.RaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/races")
@RequiredArgsConstructor
public class RaceController {

    private final RaceService raceService;

    @PostMapping("/batch")
    public ResponseEntity<List<RaceResponse>> createRacesBatch(@RequestBody RaceBatchCreateRequest request) {
        List<RaceResponse> responses = raceService.createRacesBatch(request);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse> getRaceDetailsByTournament(@PathVariable Integer tournamentId) {
        com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse response = raceService.getRaceDetailsByTournament(tournamentId);
        return ResponseEntity.ok(response);
    }
}
