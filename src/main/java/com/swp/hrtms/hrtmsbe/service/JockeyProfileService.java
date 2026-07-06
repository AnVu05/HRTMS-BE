package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.JockeyProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertificateResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyProfileResponse;

import java.util.List;

public interface JockeyProfileService {
    JockeyProfileResponse getProfile(Integer jockeyId);
    JockeyProfileResponse updateProfile(Integer jockeyId, JockeyProfileUpdateRequest request);

    // Khai: Get every certificate belonging to a jockey with its current status.
    List<JockeyCertificateResponse> getCertificates(Integer jockeyId);

    // Khai: Update a jockey certificate and require admin verification again.
    JockeyCertificateResponse updateCertificate(
            Integer jockeyId,
            Integer certId,
            JockeyCertUpdateRequest request);

    // Khai: Permanently delete a certificate belonging to a jockey.
    void deleteCertificate(Integer jockeyId, Integer certId);
}


