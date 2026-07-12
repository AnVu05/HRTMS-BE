package com.swp.hrtms.hrtmsbe.service.impl;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.HorseOwnerProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerProfileResponse;
import com.swp.hrtms.hrtmsbe.entity.HorseOwner;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.service.HorseOwnerProfileService;
//import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;

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
        return toResponse(horseOwner);
    }

    @Override
    @Transactional
    public HorseOwnerProfileResponse updateProfile(Integer ownerId, HorseOwnerProfileUpdateRequest request) {
        HorseOwner horseOwner = findHorseOwnerById(ownerId);
        User user = horseOwner.getUser();

        applyRequestToUser(user, request);

        if (request.getOwnerName() != null && !request.getOwnerName().isBlank()) {
            horseOwner.setOwnerName(request.getOwnerName());
        }

        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            horseOwner.setAvatar(request.getAvatar());
        }

        userRepository.save(user);
        HorseOwner updatedOwner = horseOwnerRepository.save(horseOwner);
        return toResponse(updatedOwner);
    }

    @Override
    @Transactional
    public HorseOwnerProfileResponse updateAvatar(Integer ownerId, String avatarBase64) {
        HorseOwner horseOwner = findHorseOwnerById(ownerId);
        if (avatarBase64 != null) {
            horseOwner.setAvatar(avatarBase64);
        }
        HorseOwner updatedOwner = horseOwnerRepository.save(horseOwner);
        return toResponse(updatedOwner);
    }

    @Override
    @Transactional
    public void deactivateAccount(Integer ownerId) {
        HorseOwner horseOwner = findHorseOwnerById(ownerId);
        User user = horseOwner.getUser();
        user.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.INACTIVE);
        userRepository.save(user);
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

        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(request.getRole());
        }

        if (request.getCreatedAt() != null) {
            user.setCreatedAt(request.getCreatedAt());
        }
    }

    // Khải: Chặn trùng username để tránh lỗi unique constraint từ database.
    private void validateUsernameIsAvailable(String username, Integer userId) {
        if (userRepository.existsByUsernameAndIdNot(username, userId)) {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    // Khải: Chặn trùng email để tránh lỗi unique constraint từ database.
    private void validateEmailIsAvailable(String email, Integer userId) {
        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    // Khi: Map entity User sang response an toAn cho mAn hAnh profile.
    private HorseOwnerProfileResponse toResponse(HorseOwner horseOwner) {
        User user = horseOwner.getUser();
        return new HorseOwnerProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                horseOwner.getOwnerName(),
                horseOwner.getAvatar());
    }
}


