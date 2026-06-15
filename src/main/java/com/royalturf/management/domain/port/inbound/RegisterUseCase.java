package com.royalturf.management.domain.port.inbound;

import com.royalturf.management.domain.model.User;

public interface RegisterUseCase {
    User register(Command command);

    record Command(
        String username,
        String email,
        String password,
        String role
    ) {}
}
