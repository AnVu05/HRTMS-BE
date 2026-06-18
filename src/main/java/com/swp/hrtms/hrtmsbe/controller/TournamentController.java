package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentDashboardResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse;
import com.swp.hrtms.hrtmsbe.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    public ResponseEntity<ApiResponse<TournamentResponse>> createTournament(
            @RequestBody TournamentCreateRequest request) {
        TournamentResponse response = tournamentService.createTournament(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Tournament created successfully"),
                HttpStatus.CREATED);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<List<TournamentDashboardResponse>>> getTournamentsForDashboard() {
        List<TournamentDashboardResponse> tournaments = tournamentService.getTournamentsForDashboard();
        return ResponseEntity.ok(ApiResponse.success(tournaments, null));
    }

    // @GetMapping("/active")
    // public ResponseEntity<ApiResponse<List<ActiveTournamentResponse>>>
    // getActiveTournaments() {
    // List<ActiveTournamentResponse> tournaments =
    // tournamentService.getActiveTournaments();
    // return ResponseEntity.ok(ApiResponse.success(tournaments, null));
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TournamentResponse>> updateTournament(
            @PathVariable("id") Integer id,
            @RequestBody com.swp.hrtms.hrtmsbe.dto.request.TournamentUpdateRequest request) {
        TournamentResponse response = tournamentService.updateTournament(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tournament updated successfully"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelTournament(@PathVariable("id") Integer id,
            @RequestBody com.swp.hrtms.hrtmsbe.dto.request.TournamentCancelRequest request) {
        String response = tournamentService.cancelTournament(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, response));
    }
}
