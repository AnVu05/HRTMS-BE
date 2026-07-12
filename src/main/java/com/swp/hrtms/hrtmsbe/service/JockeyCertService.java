package com.swp.hrtms.hrtmsbe.service;


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


