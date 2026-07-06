package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.AdminRequest;
import com.swp.hrtms.hrtmsbe.dto.response.AdminResponse;
import java.util.List;

public interface AdminService {
    AdminResponse create(AdminRequest request);
    List<AdminResponse> getAll();
    AdminResponse getById(Integer id);
    AdminResponse update(Integer id, AdminRequest request);
    void delete(Integer id);
}


