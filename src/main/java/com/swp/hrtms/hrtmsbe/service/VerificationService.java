package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertCreateRequest;
import com.swp.hrtms.hrtmsbe.entity.JockeyCert;

import java.util.List;

public interface VerificationService {
    List<JockeyCert> getJockeyVerificationRequests(Integer recipientId);
    List<JockeyCert> getPendingCertificateImages(Integer jockeyId);

    // Khai: Jockey certificate submission and verification-request operations.
    JockeyCert createJockeyCertificate(JockeyCertCreateRequest request);
    Integer requestVerificationForAll(Integer jockeyId);
    List<JockeyCert> acceptJockeyCertificates(Integer jockeyId, Integer adminId);
    List<JockeyCert> rejectJockeyCertificates(Integer jockeyId, Integer adminId, String reason);
}


