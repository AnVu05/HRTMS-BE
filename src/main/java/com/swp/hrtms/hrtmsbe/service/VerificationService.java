package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyVerificationRequestResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertImageResponse;

import java.util.List;

public interface VerificationService {
    List<JockeyVerificationRequestResponse> getJockeyVerificationRequests(Integer recipientId);
    List<JockeyCertImageResponse> getPendingCertificateImages(Integer jockeyId);

    // Khai: Jockey certificate submission and verification-request operations.
    Integer createJockeyCertificate(Integer jockeyId, JockeyCertCreateRequest request);
    Integer requestVerificationForAll(Integer jockeyId);
}
