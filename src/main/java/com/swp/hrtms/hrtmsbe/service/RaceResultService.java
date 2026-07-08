package com.swp.hrtms.hrtmsbe.service;

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
