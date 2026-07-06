package com.swp.hrtms.hrtmsbe.service.impl;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.service.AdminService;
import com.swp.hrtms.hrtmsbe.dto.request.AdminRequest;
import com.swp.hrtms.hrtmsbe.dto.response.AdminResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AdminServiceImpl(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AdminResponse create(AdminRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(request.getPassword());
        admin.setEmail(request.getEmail());
        admin.setRole(request.getRole() != null ? request.getRole() : "ADMIN");
        admin.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
        admin.setAvatar(request.getAvatar());
        //khai
        admin.setStatus(request.getStatus() != null ? request.getStatus() : com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);

        Admin savedAdmin = adminRepository.save(admin);
        return toResponse(savedAdmin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminResponse> getAll() {
        return adminRepository.findAll().stream()
                //khai
                .filter(admin -> admin.getStatus() != com.swp.hrtms.hrtmsbe.enums.UserStatus.DELETE)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminResponse getById(Integer id) {
        Admin admin = adminRepository.findById(id)
                //khai
                .filter(a -> a.getStatus() != com.swp.hrtms.hrtmsbe.enums.UserStatus.DELETE)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        return toResponse(admin);
    }

    @Override
    @Transactional
    public AdminResponse update(Integer id, AdminRequest request) {
        Admin admin = adminRepository.findById(id)
                //khai
                .filter(a -> a.getStatus() != com.swp.hrtms.hrtmsbe.enums.UserStatus.DELETE)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (userRepository.existsByUsernameAndIdNot(request.getUsername(), admin.getId())) {
                throw new IllegalArgumentException("Username already exists");
            }
            admin.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), admin.getId())) {
                throw new IllegalArgumentException("Email already exists");
            }
            admin.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            admin.setPassword(request.getPassword());
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            admin.setRole(request.getRole());
        }

        if (request.getCreatedAt() != null) {
            admin.setCreatedAt(request.getCreatedAt());
        }

        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            admin.setAvatar(request.getAvatar());
        }
        
        if (request.getStatus() != null) {
            admin.setStatus(request.getStatus());
        }

        Admin updatedAdmin = adminRepository.save(admin);
        return toResponse(updatedAdmin);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Admin admin = adminRepository.findById(id)
                //khai
                .filter(a -> a.getStatus() != com.swp.hrtms.hrtmsbe.enums.UserStatus.DELETE)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        admin.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.DELETE);
        adminRepository.save(admin);
    }

    private AdminResponse toResponse(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setPassword(admin.getPassword());
        response.setEmail(admin.getEmail());
        response.setRole(admin.getRole());
        response.setCreatedAt(admin.getCreatedAt());
        response.setAvatar(admin.getAvatar());
        response.setStatus(admin.getStatus());
        return response;
    }
}





