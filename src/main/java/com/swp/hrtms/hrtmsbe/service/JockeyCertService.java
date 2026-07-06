package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertResponse;
import java.util.List;

public interface JockeyCertService {
    JockeyCertResponse create(JockeyCertRequest request);
    List<JockeyCertResponse> getAll();
    JockeyCertResponse getById(Integer id);
    JockeyCertResponse update(Integer id, JockeyCertRequest request);
    void delete(Integer id);
}


