package com.swp.hrtms.hrtmsbe.controller;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.AdminRequest;
import com.swp.hrtms.hrtmsbe.dto.response.AdminResponse;
import com.swp.hrtms.hrtmsbe.service.AdminService;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    private final AdminService service;

    public AdminController(AdminService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminResponse>> create(@RequestBody AdminRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request), "Created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id), null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponse>> update(@PathVariable Integer id, @RequestBody AdminRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted successfully"));
    }
}


