package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.RegisterRequest;
import com.swp.hrtms.hrtmsbe.dto.response.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
}
