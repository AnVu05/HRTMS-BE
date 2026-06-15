package com.royalturf.management.infrastructure.adapter.inbound.web;

import com.royalturf.management.domain.model.User;
import com.royalturf.management.domain.port.inbound.RegisterUseCase;
import com.royalturf.management.infrastructure.adapter.inbound.web.dto.ApiResponse;
import com.royalturf.management.infrastructure.adapter.inbound.web.dto.RegisterRequest;
import com.royalturf.management.infrastructure.adapter.inbound.web.dto.UserResponse;
import com.royalturf.management.infrastructure.adapter.inbound.web.mapper.WebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class RegisterController {

    private final RegisterUseCase registerUseCase;

    public RegisterController(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUseCase.Command command = WebMapper.toCommand(request);
        User registeredUser = registerUseCase.register(command);
        UserResponse responseData = WebMapper.toResponse(registeredUser);

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .data(responseData)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Handle duplicate username/email errors and business logic exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle standard request body validation failures (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .data(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
