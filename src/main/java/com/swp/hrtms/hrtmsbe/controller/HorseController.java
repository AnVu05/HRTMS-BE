package com.swp.hrtms.hrtmsbe.controller;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.HorseRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseResponse;
import com.swp.hrtms.hrtmsbe.service.HorseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import java.util.List;

@RestController
@RequestMapping("/api/horses")
public class HorseController {

    private final HorseService horseService;

    public HorseController(HorseService horseService) {
        this.horseService = horseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HorseResponse>> createHorse(@RequestBody HorseRequest request) {
        HorseResponse response = horseService.createHorse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Horse created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HorseResponse>>> getAllHorses() {
        return ResponseEntity.ok(ApiResponse.success(horseService.getAllHorses(), null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HorseResponse>> getHorseById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(horseService.getHorseById(id), null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HorseResponse>> updateHorse(@PathVariable Integer id, @RequestBody HorseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(horseService.updateHorse(id, request), "Horse updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHorse(@PathVariable Integer id) {
        horseService.deleteHorse(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Horse deleted successfully"));
    }
}


