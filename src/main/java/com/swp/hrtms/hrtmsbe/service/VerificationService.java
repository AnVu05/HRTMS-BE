package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.response.JockeyVerificationRequestResponse;

import java.util.List;

public interface VerificationService {
    List<JockeyVerificationRequestResponse> getJockeyVerificationRequests(Integer recipientId);
}
