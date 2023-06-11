package com.springboot.boxo.service;

import com.springboot.boxo.payload.AuthResponse;
import com.springboot.boxo.payload.request.LoginGoogleRequest;
import com.springboot.boxo.payload.request.LoginRequest;
import com.springboot.boxo.payload.request.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse loginWithIdentityAndPassword(LoginRequest loginDto);
    AuthResponse register(RegisterRequest registerDto);
    AuthResponse loginWithGoogle(HttpServletRequest request, LoginGoogleRequest loginGoogleDto);
}
