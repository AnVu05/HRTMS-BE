package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyProfileResponse;
import com.swp.hrtms.hrtmsbe.entity.Jockey;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.JockeyRepository;
import com.swp.hrtms.hrtmsbe.service.JockeyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JockeyProfileServiceImpl implements JockeyProfileService {

    private final JockeyRepository jockeyRepository;

    @Override
    @Transactional(readOnly = true)
    public JockeyProfileResponse getProfile(Integer jockeyId) {
        Jockey jockey = findJockeyById(jockeyId);
        return toResponse(jockey);
    }

    @Override
    @Transactional
    public JockeyProfileResponse updateProfile(Integer jockeyId, JockeyProfileUpdateRequest request) {
        Jockey jockey = findJockeyById(jockeyId);

        applyRequestToJockey(jockey, request);

        Jockey updatedJockey = jockeyRepository.save(jockey);
        return toResponse(updatedJockey);
    }

    private Jockey findJockeyById(Integer jockeyId) {
        return jockeyRepository.findById(jockeyId)
                .orElseThrow(() -> new ResourceNotFoundException("Jockey not found with id: " + jockeyId));
    }

    private void applyRequestToJockey(Jockey jockey, JockeyProfileUpdateRequest request) {
        if (request.getJockeyName() != null) {
            jockey.setJockeyName(request.getJockeyName().trim());
        }

        if (request.getYearOfExperience() != null) {
            if (request.getYearOfExperience() < 0) {
                throw new IllegalArgumentException("Years of experience must be greater than or equal to 0");
            }
            jockey.setYearOfExperience(request.getYearOfExperience());
        }

        if (request.getAge() != null) {
            if (request.getAge() < 0) {
                throw new IllegalArgumentException("Age must be greater than or equal to 0");
            }
            jockey.setAge(request.getAge());
        }

        if (request.getProfessionalBio() != null) {
            jockey.setProfessionalBio(request.getProfessionalBio().trim());
        }
    }

    private JockeyProfileResponse toResponse(Jockey jockey) {
        return JockeyProfileResponse.builder()
                .id(jockey.getId())
                .username(jockey.getUsername())
                .email(jockey.getEmail())
                .role(jockey.getRole())
                .createdAt(jockey.getCreatedAt())
                .jockeyName(jockey.getJockeyName())
                .yearOfExperience(jockey.getYearOfExperience())
                .age(jockey.getAge())
                .professionalBio(jockey.getProfessionalBio())
                .status(jockey.getStatus())
                .build();
    }
}
