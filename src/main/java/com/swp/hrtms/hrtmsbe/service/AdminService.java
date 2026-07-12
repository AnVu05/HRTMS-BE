package com.swp.hrtms.hrtmsbe.service;

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


