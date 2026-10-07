package com.suraj.ecommerce.controller;

import com.suraj.ecommerce.dto.ApiResponse;
import com.suraj.ecommerce.dto.AuthResponse;
import com.suraj.ecommerce.dto.LoginRequest;
import com.suraj.ecommerce.dto.RegisterRequest;
import com.suraj.ecommerce.service.AuthService;

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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);

        return new ResponseEntity<>(
                ApiResponse.success(
                        "Registration successful",
                        response
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        response
                )
        );
    }
}