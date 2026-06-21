package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.SpectatorAvatarUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.SpectatorProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.SpectatorProfileResponse;
import com.swp.hrtms.hrtmsbe.entity.Spectator;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.SpectatorRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.service.SpectatorProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpectatorProfileServiceImpl implements SpectatorProfileService {

    private final SpectatorRepository spectatorRepository;
    private final UserRepository userRepository;

    public SpectatorProfileServiceImpl(SpectatorRepository spectatorRepository, UserRepository userRepository) {
        this.spectatorRepository = spectatorRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SpectatorProfileResponse getProfile(Integer spectatorId) {
        return toResponse(findSpectatorById(spectatorId));
    }

    @Override
    @Transactional
    public SpectatorProfileResponse updateProfile(Integer spectatorId, SpectatorProfileUpdateRequest request) {
        Spectator spectator = findSpectatorById(spectatorId);

        applyProfileRequest(spectator, request);

        Spectator updatedSpectator = spectatorRepository.save(spectator);
        return toResponse(updatedSpectator);
    }

    @Override
    @Transactional
    public SpectatorProfileResponse updateAvatar(Integer spectatorId, SpectatorAvatarUpdateRequest request) {
        Spectator spectator = findSpectatorById(spectatorId);

        if (request.getAvatarBase64() == null || request.getAvatarBase64().isBlank()) {
            throw new IllegalArgumentException("Avatar image cannot be empty");
        }

        spectator.setAvatarImage(request.getAvatarBase64().trim());

        Spectator updatedSpectator = spectatorRepository.save(spectator);
        return toResponse(updatedSpectator);
    }

    private Spectator findSpectatorById(Integer spectatorId) {
        return spectatorRepository.findById(spectatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Spectator not found with id: " + spectatorId));
    }

    private void applyProfileRequest(Spectator spectator, SpectatorProfileUpdateRequest request) {
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            validateUsernameIsAvailable(request.getUsername(), spectator.getId());
            spectator.setUsername(request.getUsername());
            spectator.setDisplayName(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            validateEmailIsAvailable(request.getEmail(), spectator.getId());
            spectator.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            spectator.setPassword(request.getPassword());
        }
    }

    private void validateUsernameIsAvailable(String username, Integer userId) {
        if (userRepository.existsByUsernameAndIdNot(username, userId)) {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    private void validateEmailIsAvailable(String email, Integer userId) {
        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private SpectatorProfileResponse toResponse(Spectator spectator) {
        return new SpectatorProfileResponse(
                spectator.getId(),
                spectator.getUsername(),
                spectator.getUsername(),
                spectator.getEmail(),
                spectator.getRole(),
                spectator.getCreatedAt(),
                spectator.getAvatarImage());
    }
}
