package com.royalturf.management.infrastructure.adapter.inbound.web.mapper;

import com.royalturf.management.domain.model.User;
import com.royalturf.management.domain.port.inbound.RegisterUseCase;
import com.royalturf.management.infrastructure.adapter.inbound.web.dto.RegisterRequest;
import com.royalturf.management.infrastructure.adapter.inbound.web.dto.UserResponse;

public final class WebMapper {

    private WebMapper() {
        // Prevent instantiation
    }

    public static RegisterUseCase.Command toCommand(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        return new RegisterUseCase.Command(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
