package com.swp.hrtms.hrtmsbe.controller;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ActiveTournamentResponse;
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

    @PostMapping("/{adminId}")
    public ResponseEntity<ApiResponse<TournamentResponse>> createTournament(
            @PathVariable("adminId") Integer adminId,
            @RequestBody TournamentCreateRequest request) {
        TournamentResponse response = tournamentService.createTournament(adminId, request);
        return new ResponseEntity<>(ApiResponse.success(response, "Tournament created successfully"),
                HttpStatus.CREATED);
    }

    //khai
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<List<TournamentResponse>>> getTournamentsForDashboard() {
        List<TournamentResponse> tournaments = tournamentService.getTournamentsForDashboard();
        return ResponseEntity.ok(ApiResponse.success(tournaments, null));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ActiveTournamentResponse>>> getActiveTournaments() {
        List<ActiveTournamentResponse> tournaments = tournamentService.getActiveTournaments();
        return ResponseEntity.ok(ApiResponse.success(tournaments, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TournamentResponse>> getTournamentById(@PathVariable("id") Integer id) {
        TournamentResponse tournament = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(ApiResponse.success(tournament, null));
    }

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


