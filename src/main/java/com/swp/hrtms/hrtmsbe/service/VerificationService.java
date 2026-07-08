package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertCreateRequest;
import com.swp.hrtms.hrtmsbe.entity.JockeyCert;

import java.util.List;

import com.swp.hrtms.hrtmsbe.entity.JockeyCert;

public interface VerificationService {
    List<JockeyCert> getJockeyVerificationRequests(Integer recipientId);
    List<com.swp.hrtms.hrtmsbe.dto.response.JockeyCertImageResponse> getPendingCertificateImages(Integer jockeyId);

    // Khai: Jockey certificate submission and verification-request operations.
    JockeyCert createJockeyCertificate(JockeyCertCreateRequest request);
    Integer requestVerificationForAll(Integer jockeyId);
    List<JockeyCert> acceptJockeyCertificates(Integer jockeyId, Integer adminId);
    List<JockeyCert> rejectJockeyCertificates(Integer jockeyId, Integer adminId, String reason);
}


