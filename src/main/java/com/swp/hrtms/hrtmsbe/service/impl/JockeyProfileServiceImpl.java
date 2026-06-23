package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertificateResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyProfileResponse;
import com.swp.hrtms.hrtmsbe.entity.Jockey;
import com.swp.hrtms.hrtmsbe.entity.JockeyCert;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository;
import com.swp.hrtms.hrtmsbe.repository.JockeyRepository;
import com.swp.hrtms.hrtmsbe.service.JockeyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JockeyProfileServiceImpl implements JockeyProfileService {

    private final JockeyRepository jockeyRepository;
    private final JockeyCertRepository jockeyCertRepository;

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

    // Khai: Read all certificates without filtering out statuses updated by an admin.
    @Override
    @Transactional(readOnly = true)
    public List<JockeyCertificateResponse> getCertificates(Integer jockeyId) {
        findJockeyById(jockeyId);

        return jockeyCertRepository.findAllCertificatesByJockeyId(jockeyId)
                .stream()
                .map(this::toCertificateResponse)
                .toList();
    }

    // Khai: Updating verified data resets the certificate to PENDING for admin review.
    @Override
    @Transactional
    public JockeyCertificateResponse updateCertificate(
            Integer jockeyId,
            Integer certId,
            JockeyCertUpdateRequest request) {
        validateCertificateUpdateRequest(request);
        JockeyCert certificate = findCertificateByIdAndJockeyId(certId, jockeyId);

        certificate.setCertName(request.getCertName().trim());
        certificate.setCertImg(request.getCertImageBase64());
        certificate.setStatus("PENDING");

        return toCertificateResponse(jockeyCertRepository.save(certificate));
    }

    // Khai: Permanently remove the certificate from the database.
    @Override
    @Transactional
    public void deleteCertificate(Integer jockeyId, Integer certId) {
        JockeyCert certificate = findCertificateByIdAndJockeyId(certId, jockeyId);
        jockeyCertRepository.delete(certificate);
    }

    private Jockey findJockeyById(Integer jockeyId) {
        return jockeyRepository.findById(jockeyId)
                .orElseThrow(() -> new ResourceNotFoundException("Jockey not found with id: " + jockeyId));
    }

    private JockeyCert findCertificateByIdAndJockeyId(Integer certId, Integer jockeyId) {
        return jockeyCertRepository.findCertificateByIdAndJockeyId(certId, jockeyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate not found with id " + certId + " for jockey " + jockeyId));
    }

    private void validateCertificateUpdateRequest(JockeyCertUpdateRequest request) {
        if (request == null || request.getCertName() == null || request.getCertName().isBlank()) {
            throw new IllegalArgumentException("Certificate name cannot be empty");
        }
        if (request.getCertImageBase64() == null || request.getCertImageBase64().isBlank()) {
            throw new IllegalArgumentException("Certificate image cannot be empty");
        }
    }

    private void applyRequestToJockey(Jockey jockey, JockeyProfileUpdateRequest request) {
        if (request.getJockeyName() == null || request.getJockeyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Jockey name is required");
        }

        if (request.getYearOfExperience() == null) {
            throw new IllegalArgumentException("Years of experience is required");
        }
        if (request.getYearOfExperience() < 0) {
            throw new IllegalArgumentException("Years of experience must be greater than or equal to 0");
        }

        if (request.getAge() == null) {
            throw new IllegalArgumentException("Age is required");
        }
        if (request.getAge() < 0) {
            throw new IllegalArgumentException("Age must be greater than or equal to 0");
        }

        jockey.setJockeyName(request.getJockeyName().trim());
        jockey.setYearOfExperience(request.getYearOfExperience());
        jockey.setAge(request.getAge());

        if (request.getProfessionalBio() != null) {
            jockey.setProfessionalBio(request.getProfessionalBio().trim());
        } else {
            jockey.setProfessionalBio(null);
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

    private JockeyCertificateResponse toCertificateResponse(JockeyCert certificate) {
        return JockeyCertificateResponse.builder()
                .certId(certificate.getId())
                .certName(certificate.getCertName())
                .certImageBase64(certificate.getCertImg())
                .status(certificate.getStatus())
                .build();
    }
}
