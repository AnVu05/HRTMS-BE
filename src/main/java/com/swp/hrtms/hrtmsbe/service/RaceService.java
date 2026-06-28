package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResponse;
//Khai
import com.swp.hrtms.hrtmsbe.dto.response.SingleRaceCreateResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse;

import com.swp.hrtms.hrtmsbe.dto.request.RaceCancelRequest;

import java.util.List;

public interface RaceService {
    List<RaceResponse> createRacesBatch(RaceBatchCreateRequest request);
    TournamentRaceDetailsResponse getRaceDetailsByTournament(Integer tournamentId);
    String cancelRace(Integer raceId, RaceCancelRequest request);
    //Khai
    SingleRaceCreateResponse createSingleRace(com.swp.hrtms.hrtmsbe.dto.request.SingleRaceCreateRequest request);
    RaceResponse updateRace(Integer raceId, com.swp.hrtms.hrtmsbe.dto.request.RaceUpdateRequest request);
    String updateRaceTime(Integer raceId, com.swp.hrtms.hrtmsbe.dto.request.RaceUpdateTimeRequest request);
    void predictScheduleUpdate();
}
