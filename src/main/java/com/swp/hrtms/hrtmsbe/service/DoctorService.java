package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.DoctorRequest;
import com.swp.hrtms.hrtmsbe.dto.response.DoctorResponse;
import java.util.List;

public interface DoctorService {
    DoctorResponse create(DoctorRequest request);
    List<DoctorResponse> getAll();
    DoctorResponse getById(Integer id);
    DoctorResponse update(Integer id, DoctorRequest request);
    void delete(Integer id);
}


