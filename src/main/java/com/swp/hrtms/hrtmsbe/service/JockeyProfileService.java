package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertificateResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyProfileResponse;

import java.util.List;

public interface JockeyProfileService {
    JockeyProfileResponse getProfile(Integer jockeyId);
    JockeyProfileResponse updateProfile(Integer jockeyId, JockeyProfileUpdateRequest request);

    // Khai: Get every certificate belonging to a jockey with its current status.
    List<JockeyCertificateResponse> getCertificates(Integer jockeyId);
}
