package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.RaceRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<RaceResponse>>> createRacesBatch(
            @RequestBody RaceBatchCreateRequest request) {
        List<RaceResponse> responses = raceService.createRacesBatch(request);
        return new ResponseEntity<>(ApiResponse.success(responses, "Races created successfully"), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RaceResponse>> createSingleRace(@RequestBody RaceRequest request) {
        RaceResponse response = raceService.createSingleRace(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Race created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RaceResponse>>> getAllRaces() {
        List<RaceResponse> responses = raceService.getAllRaces();
        return ResponseEntity.ok(ApiResponse.success(responses, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RaceResponse>> getRaceById(@PathVariable("id") Integer id) {
        RaceResponse response = raceService.getRaceById(id);
        return ResponseEntity.ok(ApiResponse.success(response, null));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<ApiResponse<com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse>> getRaceDetailsByTournament(
            @PathVariable Integer tournamentId) {
        com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse response = raceService
                .getRaceDetailsByTournament(tournamentId);
        return ResponseEntity.ok(ApiResponse.success(response, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RaceResponse>> updateRace(@PathVariable("id") Integer id,
            @RequestBody RaceRequest request) {
        RaceResponse response = raceService.updateRace(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Race updated successfully"));
    }

    @PutMapping("/{id}/late-scratch")
    public ResponseEntity<ApiResponse<RaceResponse>> lateScratch(@PathVariable Integer id,
            @RequestParam Integer horseId, @RequestParam String reason) {
        RaceResponse race = raceService.lateScratch(id, horseId, reason);
        return ResponseEntity.ok(ApiResponse.<RaceResponse>builder()
                .status("success")
                .message("Late scratch processed successfully.")
                .data(race)
                .build());
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse<RaceResponse>> startRace(@PathVariable Integer id) {
        RaceResponse race = raceService.startRace(id);
        return ResponseEntity.ok(ApiResponse.<RaceResponse>builder()
                .status("success")
                .message("Race started successfully.")
                .data(race)
                .build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelRace(@PathVariable("id") Integer id,
            @RequestBody com.swp.hrtms.hrtmsbe.dto.request.RaceCancelRequest request) {
        String response = raceService.cancelRace(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, response));
    }

    @PutMapping("/{id}/time")
    public ResponseEntity<ApiResponse<String>> updateRaceTime(@PathVariable("id") Integer id,
            @RequestBody com.swp.hrtms.hrtmsbe.dto.request.RaceUpdateTimeRequest request) {
        String response = raceService.updateRaceTime(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, response));
    }
}
