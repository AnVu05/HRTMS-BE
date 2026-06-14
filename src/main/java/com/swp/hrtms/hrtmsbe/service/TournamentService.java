package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentDashboardResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse;

import java.util.List;

public interface TournamentService {
    TournamentResponse createTournament(TournamentCreateRequest request);
    List<TournamentDashboardResponse> getTournamentsForDashboard();
}
