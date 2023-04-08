package com.springboot.boxo.controller;

import com.springboot.boxo.payload.AuthResponse;
import com.springboot.boxo.payload.LoginDto;
import com.springboot.boxo.payload.RegisterDto;
import com.springboot.boxo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Build Login REST API
    @PostMapping(value = {"/login", "/sign-in"})
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(authService.loginWithIdentityAndPassword(loginDto));
    }

    // Build Register REST API
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterDto registerDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerDto));
    }
}