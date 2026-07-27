package com.swp.hrtms.hrtmsbe.controller;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeScheduledRaceResponse;
import com.swp.hrtms.hrtmsbe.service.RefereeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/referees")
@RequiredArgsConstructor
public class RefereeController {

    private final RefereeService refereeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RefereeResponse>>> getReferees(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) Integer excludeRaceId) {

        List<RefereeResponse> responses = refereeService.getReferees(date, startTime, endTime, excludeRaceId);
        return ResponseEntity.ok(ApiResponse.success(responses, "Referees fetched successfully"));
    }

    @GetMapping("/{refereeId}/scheduled-races")
    public ResponseEntity<ApiResponse<List<RefereeScheduledRaceResponse>>> getScheduledRaces(
            @PathVariable Integer refereeId) {
        // Lấy danh sách cuộc đua đã lên lịch và xác nhận của trọng tài chỉ định
        List<RefereeScheduledRaceResponse> responses = refereeService.getScheduledRaces(refereeId);

        // Trả về kết quả thành công với định dạng ApiResponse chuẩn
        return ResponseEntity.ok(ApiResponse.success(responses, "Scheduled races fetched successfully"));
    }
}
