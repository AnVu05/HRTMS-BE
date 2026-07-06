package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.RaceResultRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResultResponse;
import java.util.List;

public interface RaceResultService {
    RaceResultResponse create(RaceResultRequest request);
    List<RaceResultResponse> getAll();
    RaceResultResponse getById(Integer id);
    RaceResultResponse update(Integer id, RaceResultRequest request);
    void delete(Integer id);
}


