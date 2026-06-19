package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyProfileResponse;

public interface JockeyProfileService {
    JockeyProfileResponse getProfile(Integer jockeyId);
    JockeyProfileResponse updateProfile(Integer jockeyId, JockeyProfileUpdateRequest request);
}
