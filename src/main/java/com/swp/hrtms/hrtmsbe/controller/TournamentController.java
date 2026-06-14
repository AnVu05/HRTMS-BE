package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse;
import com.swp.hrtms.hrtmsbe.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(@RequestBody TournamentCreateRequest request) {
        TournamentResponse response = tournamentService.createTournament(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
