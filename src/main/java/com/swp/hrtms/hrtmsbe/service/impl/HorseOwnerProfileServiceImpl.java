package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.HorseOwnerProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerProfileResponse;
import com.swp.hrtms.hrtmsbe.entity.HorseOwner;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.service.HorseOwnerProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HorseOwnerProfileServiceImpl implements HorseOwnerProfileService {

    private final HorseOwnerRepository horseOwnerRepository;
    private final UserRepository userRepository;

    // Khải: Inject repository để lấy chủ ngựa và cập nhật thông tin tài khoản.
    public HorseOwnerProfileServiceImpl(HorseOwnerRepository horseOwnerRepository, UserRepository userRepository) {
        this.horseOwnerRepository = horseOwnerRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public HorseOwnerProfileResponse getProfile(Integer ownerId) {
        HorseOwner horseOwner = findHorseOwnerById(ownerId);
        return toResponse(horseOwner.getUser());
    }

    @Override
    @Transactional
    public HorseOwnerProfileResponse updateProfile(Integer ownerId, HorseOwnerProfileUpdateRequest request) {
        HorseOwner horseOwner = findHorseOwnerById(ownerId);
        User user = horseOwner.getUser();

        applyRequestToUser(user, request);

        User updatedUser = userRepository.save(user);
        return toResponse(updatedUser);
    }

    // Khải: Tìm đúng chủ ngựa theo user_id, nếu không có thì trả lỗi 404.
    private HorseOwner findHorseOwnerById(Integer ownerId) {
        return horseOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse owner not found with id: " + ownerId));
    }

    // Khải: Chỉ cập nhật password khi frontend gửi giá trị mới.
    private void applyRequestToUser(User user, HorseOwnerProfileUpdateRequest request) {
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            validateUsernameIsAvailable(request.getUsername(), user.getId());
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            validateEmailIsAvailable(request.getEmail(), user.getId());
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }
    }

    // Khải: Chặn trùng username để tránh lỗi unique constraint từ database.
    private void validateUsernameIsAvailable(String username, Integer userId) {
        if (userRepository.existsByUsernameAndIdNot(username, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
    }

    // Khải: Chặn trùng email để tránh lỗi unique constraint từ database.
    private void validateEmailIsAvailable(String email, Integer userId) {
        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    // Khải: Map entity User sang response an toàn cho màn hình profile.
    private HorseOwnerProfileResponse toResponse(User user) {
        return new HorseOwnerProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt());
    }
}
