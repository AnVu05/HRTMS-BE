package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.HorseRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseResponse;

import java.util.List;

public interface HorseService {

    HorseResponse createHorse(HorseRequest request);

    List<HorseResponse> getAllHorses();

    HorseResponse getHorseById(Integer id);

    HorseResponse updateHorse(Integer id, HorseRequest request);

    void deleteHorse(Integer id);
}
