package com.springboot.boxo.service;

import com.springboot.boxo.payload.AuthResponse;
import com.springboot.boxo.payload.LoginDto;
import com.springboot.boxo.payload.RegisterDto;

public interface AuthService {
    AuthResponse loginWithIdentityAndPassword(LoginDto loginDto);

    AuthResponse register(RegisterDto registerDto);
}
