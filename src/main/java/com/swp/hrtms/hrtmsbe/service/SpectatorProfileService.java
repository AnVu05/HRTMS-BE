package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.SpectatorAvatarUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.SpectatorProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.SpectatorProfileResponse;

public interface SpectatorProfileService {

    SpectatorProfileResponse getProfile(Integer spectatorId);

    SpectatorProfileResponse updateProfile(Integer spectatorId, SpectatorProfileUpdateRequest request);

    SpectatorProfileResponse updateAvatar(Integer spectatorId, SpectatorAvatarUpdateRequest request);
}


