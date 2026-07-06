package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.PredictionRequest;
import com.swp.hrtms.hrtmsbe.dto.response.PredictionResponse;
import java.util.List;

public interface PredictionService {
    PredictionResponse create(PredictionRequest request);
    List<PredictionResponse> getAll();
    PredictionResponse getById(Integer id);
    PredictionResponse update(Integer id, PredictionRequest request);
    void delete(Integer id);
}


