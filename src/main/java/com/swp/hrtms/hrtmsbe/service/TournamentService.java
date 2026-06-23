package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.TournamentCancelRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ActiveTournamentResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentDashboardResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse;

import java.util.List;

public interface TournamentService {
    TournamentResponse createTournament(Integer adminId, TournamentCreateRequest request);

    List<TournamentDashboardResponse> getTournamentsForDashboard();

    TournamentResponse updateTournament(Integer id, com.swp.hrtms.hrtmsbe.dto.request.TournamentUpdateRequest request);

    List<ActiveTournamentResponse> getActiveTournaments();

    String cancelTournament(Integer tournamentId, TournamentCancelRequest request);

    void updateTournamentStatuses();
}
