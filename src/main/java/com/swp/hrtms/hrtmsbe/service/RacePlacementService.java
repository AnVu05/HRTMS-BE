package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
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


