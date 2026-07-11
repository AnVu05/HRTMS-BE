package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.HorseRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseResponse;

import java.util.List;

public interface HorseService {

    HorseResponse createHorse(HorseRequest request);

    List<HorseResponse> getAllHorses();

    List<HorseResponse> getHorsesByOwner(Integer ownerId);

    List<HorseResponse> getWorkingHorsesByOwner(Integer ownerId);

    HorseResponse getHorseById(Integer id);

    HorseResponse updateHorse(Integer id, HorseRequest request);

    void deleteHorse(Integer id);
}


