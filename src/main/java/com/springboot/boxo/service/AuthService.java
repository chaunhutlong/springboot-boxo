package com.springboot.boxo.service;

import com.springboot.boxo.payload.LoginDto;
import com.springboot.boxo.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
