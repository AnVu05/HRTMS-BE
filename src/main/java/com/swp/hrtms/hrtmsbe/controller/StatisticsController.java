package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.response.AdminDashboardStatsResponse;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/admin/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getAdminDashboardStats() {
        AdminDashboardStatsResponse response = statisticsService.getAdminDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(response, "Fetched statistics successfully"));
    }
}
