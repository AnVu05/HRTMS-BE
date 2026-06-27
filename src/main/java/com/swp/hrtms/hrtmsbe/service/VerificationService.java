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
    // Khai: Old methods accepted/rejected every pending certificate of one jockey.
    // void acceptJockeyCertificates(Integer jockeyId, Integer adminId);
    // void rejectJockeyCertificates(Integer jockeyId, Integer adminId, String reason);

    // Khai: Accept or reject only one selected certificate.
    void acceptJockeyCertificate(Integer jockeyId, Integer certId, Integer adminId);
    void rejectJockeyCertificate(Integer jockeyId, Integer certId, Integer adminId, String reason);
}
