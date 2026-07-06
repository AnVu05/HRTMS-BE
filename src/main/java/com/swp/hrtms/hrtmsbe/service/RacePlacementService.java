package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.RacePlacementRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RacePlacementResponse;
import java.util.List;

public interface RacePlacementService {
    RacePlacementResponse create(RacePlacementRequest request);
    List<RacePlacementResponse> getAll();
    RacePlacementResponse getById(Integer id);
    RacePlacementResponse update(Integer id, RacePlacementRequest request);
    void delete(Integer id);
}


