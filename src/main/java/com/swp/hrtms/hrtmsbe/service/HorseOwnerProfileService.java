package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.HorseOwnerProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerProfileResponse;

// Khải: Service xử lý chức năng xem và cập nhật profile của chủ ngựa.
public interface HorseOwnerProfileService {

    HorseOwnerProfileResponse getProfile(Integer ownerId);

    HorseOwnerProfileResponse updateProfile(Integer ownerId, HorseOwnerProfileUpdateRequest request);
}


