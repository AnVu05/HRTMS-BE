package com.swp.hrtms.hrtmsbe.controller;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadBase64(@RequestBody Map<String, String> request) {
        String base64Image = request.get("image");
        if (base64Image == null || base64Image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Image data is empty"));
        }

        // Thay vì lưu xuống ổ cứng, API này sẽ trả về chính chuỗi Base64
        // Frontend sẽ dùng chuỗi Base64 này như một "URL" để gửi cho các API khác (như cập nhật Avatar)
        // và Backend sẽ lưu toàn bộ chuỗi Base64 này trực tiếp vào Database.
        return ResponseEntity.ok(ApiResponse.success(base64Image, "Base64 image processed successfully"));
    }
}


