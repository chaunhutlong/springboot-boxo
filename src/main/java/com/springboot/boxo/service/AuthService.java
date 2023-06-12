package com.springboot.boxo.service;

import com.springboot.boxo.payload.AuthResponse;
import com.springboot.boxo.payload.request.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse loginWithIdentityAndPassword(LoginRequest loginDto);
    AuthResponse register(RegisterRequest registerDto);
    AuthResponse loginWithGoogle(HttpServletRequest request, LoginGoogleRequest loginGoogleDto);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void resetPassword(String token, ResetPasswordRequest request);
}
