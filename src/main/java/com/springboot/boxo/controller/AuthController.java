package com.springboot.boxo.controller;

import com.springboot.boxo.payload.AuthResponse;
import com.springboot.boxo.payload.request.*;
import com.springboot.boxo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Build Login REST API
    @PostMapping(value = {"/login", "/sign-in"})
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginDto){
        return ResponseEntity.ok(authService.loginWithIdentityAndPassword(loginDto));
    }

    // Build Register REST API
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerDto));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(HttpServletRequest request, @RequestBody LoginGoogleRequest loginGoogleRequest){
        return ResponseEntity.ok(authService.loginWithGoogle(request, loginGoogleRequest));
    }

    // Build Forgot Password REST API
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request){
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    // Build Reset Password REST API
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam(value = "token") String token, @RequestBody ResetPasswordRequest request){
        authService.resetPassword(token, request);
        return ResponseEntity.ok().build();
    }

}