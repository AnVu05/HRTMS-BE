package com.swp.hrtms.hrtmsbe.controller;


import com.swp.hrtms.hrtmsbe.dto.request.RacePlacementRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RacePlacementResponse;
import com.swp.hrtms.hrtmsbe.service.RacePlacementService;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/raceplacements")
public class RacePlacementController {
    private final RacePlacementService service;

    public RacePlacementController(RacePlacementService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ApiResponse<RacePlacementResponse>> create(@RequestBody RacePlacementRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request), "Created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RacePlacementResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RacePlacementResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id), null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RacePlacementResponse>> update(@PathVariable Integer id, @RequestBody RacePlacementRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted successfully"));
    }
}


