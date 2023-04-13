package com.springboot.boxo.service;

import com.springboot.boxo.payload.AuthResponse;
import com.springboot.boxo.payload.LoginRequest;
import com.springboot.boxo.payload.RegisterRequest;

public interface AuthService {
    AuthResponse loginWithIdentityAndPassword(LoginRequest loginDto);
    AuthResponse register(RegisterRequest registerDto);
}
