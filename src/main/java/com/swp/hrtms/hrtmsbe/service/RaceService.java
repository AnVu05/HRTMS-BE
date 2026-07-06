package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResponse;
import com.swp.hrtms.hrtmsbe.dto.response.SingleRaceCreateResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse;
import com.swp.hrtms.hrtmsbe.dto.request.RaceCancelRequest;

import java.util.List;

public interface RaceService {
    List<RaceResponse> createRacesBatch(RaceBatchCreateRequest request);
    TournamentRaceDetailsResponse getRaceDetailsByTournament(Integer tournamentId);
    String cancelRace(Integer raceId, RaceCancelRequest request);
    
    //Khai
    RaceResponse createSingleRace(com.swp.hrtms.hrtmsbe.dto.request.RaceRequest request);
    List<RaceResponse> getAllRaces();
    RaceResponse getRaceById(Integer id);
    RaceResponse updateRace(Integer raceId, com.swp.hrtms.hrtmsbe.dto.request.RaceRequest request);
    
    String walkOverRace(Integer raceId);
    String updateRaceTime(Integer raceId, com.swp.hrtms.hrtmsbe.dto.request.RaceUpdateTimeRequest request);
    void predictScheduleUpdate();
    RaceResponse lateScratch(Integer raceId, Integer horseId, String reason);
    RaceResponse startRace(Integer raceId);
}




