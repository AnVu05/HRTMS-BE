package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.RaceFormatRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceFormatResponse;
import java.util.List;

public interface RaceFormatService {
    RaceFormatResponse create(RaceFormatRequest request);
    List<RaceFormatResponse> getAll();
    RaceFormatResponse getById(Integer id);
    RaceFormatResponse update(Integer id, RaceFormatRequest request);
    void delete(Integer id);
}


