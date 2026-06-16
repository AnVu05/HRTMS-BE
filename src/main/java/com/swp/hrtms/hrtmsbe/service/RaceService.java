package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse;

import com.swp.hrtms.hrtmsbe.dto.request.RaceCancelRequest;

import java.util.List;

public interface RaceService {
    List<RaceResponse> createRacesBatch(RaceBatchCreateRequest request);
    TournamentRaceDetailsResponse getRaceDetailsByTournament(Integer tournamentId);
    String cancelRace(Integer raceId, RaceCancelRequest request);
}
