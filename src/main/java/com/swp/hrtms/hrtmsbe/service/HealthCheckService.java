package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.HealthCheckRequest;
import com.swp.hrtms.hrtmsbe.dto.request.HealthCheckCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HealthCheckResponse;
import java.util.List;

public interface HealthCheckService {
    HealthCheckResponse create(HealthCheckCreateRequest request);
    List<HealthCheckResponse> getAll();
    HealthCheckResponse getById(Integer id);
    HealthCheckResponse update(Integer id, HealthCheckRequest request);
    void delete(Integer id);
    
    java.util.List<HealthCheckResponse> getAssignedHealthChecks(Integer doctorId);
    
    HealthCheckResponse processHealthCheck(Integer id, com.swp.hrtms.hrtmsbe.dto.request.HealthCheckProcessRequest request);
}

