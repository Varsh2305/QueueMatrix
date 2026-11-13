package com.queuematrix.backend.controller;

import com.queuematrix.backend.dto.request.LoginRequest;
import com.queuematrix.backend.dto.request.RegisterRequest;
import com.queuematrix.backend.dto.response.ApiResponse;
import com.queuematrix.backend.dto.response.AuthResponse;
import com.queuematrix.backend.dto.response.UserSummaryDto;
import com.queuematrix.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User registration without OTP.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserSummaryDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserSummaryDto user = authService.register(request);
        String message = switch (user.getRole()) {
            case CUSTOMER -> "Registration completed successfully! You can now log in.";
            case STAFF -> "Registration submitted! Your Staff account is pending authorized invitation/approval.";
            default -> "Registration successful.";
        };
        return new ResponseEntity<>(ApiResponse.success(message, user), HttpStatus.CREATED);
    }

    /**
     * Password-based login using email and password. Issues JWT access token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
