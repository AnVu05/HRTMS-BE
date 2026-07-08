package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.RegistrationFormRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RegistrationFormResponse;
import com.swp.hrtms.hrtmsbe.service.RegistrationFormService;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/registrationforms")
public class RegistrationFormController {
    private final RegistrationFormService service;

    public RegistrationFormController(RegistrationFormService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RegistrationFormResponse>> create(@RequestBody RegistrationFormRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request), "Created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RegistrationFormResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), null));
    }

    @GetMapping("/pending-admin/{adminId}")
    public ResponseEntity<ApiResponse<List<RegistrationFormResponse>>> getPendingAdminForms(
            @PathVariable Integer adminId) {
        return ResponseEntity.ok(ApiResponse.success(service.getPendingAdminForms(adminId),
                "Fetched pending admin registration forms successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistrationFormResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id), null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistrationFormResponse>> update(@PathVariable Integer id,
            @RequestBody RegistrationFormRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Updated successfully"));
    }

    @PutMapping("/{id}/admin-respond")
    public ResponseEntity<ApiResponse<RegistrationFormResponse>> adminRespond(
            @PathVariable Integer id,
            @RequestBody com.swp.hrtms.hrtmsbe.dto.request.AdminRespondRequest request) {
        RegistrationFormResponse response = service.adminRespond(id, request);
        return ResponseEntity.ok(ApiResponse.<RegistrationFormResponse>builder()
                .status("success")
                .message("Registration form admin response processed.")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        // khai
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/jockey-respond")
    public ResponseEntity<ApiResponse<RegistrationFormResponse>> jockeyRespond(
            @PathVariable Integer id,
            @RequestBody com.swp.hrtms.hrtmsbe.dto.request.JockeyRespondRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.jockeyRespond(id, request), "Responded successfully"));
    }
}
