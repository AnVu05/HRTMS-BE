package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.PredictionRequest;
import com.swp.hrtms.hrtmsbe.dto.response.PredictionResponse;
import java.util.List;

public interface PredictionService {
    PredictionResponse create(PredictionRequest request);
    List<PredictionResponse> getAll();
    List<PredictionResponse> getByUserId(Integer userId);
    PredictionResponse update(Integer id, PredictionRequest request);
    void delete(Integer id);
}


