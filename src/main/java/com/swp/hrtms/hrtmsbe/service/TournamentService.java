package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse;

public interface TournamentService {
    TournamentResponse createTournament(TournamentCreateRequest request);
}
