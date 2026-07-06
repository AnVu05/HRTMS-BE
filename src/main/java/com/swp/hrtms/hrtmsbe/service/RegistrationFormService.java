package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.RegistrationFormRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RegistrationFormResponse;
import java.util.List;

public interface RegistrationFormService {
    RegistrationFormResponse create(RegistrationFormRequest request);
    List<RegistrationFormResponse> getAll();
    RegistrationFormResponse getById(Integer id);
    RegistrationFormResponse update(Integer id, RegistrationFormRequest request);
    void delete(Integer id);
    RegistrationFormResponse jockeyRespond(Integer id, com.swp.hrtms.hrtmsbe.dto.request.JockeyRespondRequest request);
    RegistrationFormResponse adminRespond(Integer id, com.swp.hrtms.hrtmsbe.dto.request.AdminRespondRequest request);
}
