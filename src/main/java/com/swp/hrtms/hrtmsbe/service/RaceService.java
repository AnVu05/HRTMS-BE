package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResponse;

import java.util.List;

public interface RaceService {
    List<RaceResponse> createRacesBatch(RaceBatchCreateRequest request);
}
