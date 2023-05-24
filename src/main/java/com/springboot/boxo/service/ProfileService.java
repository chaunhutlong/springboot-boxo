package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.ProfileDTO;
import com.springboot.boxo.payload.request.ProfileRequest;

public interface ProfileService {
    ProfileDTO getProfileByUserId(Long userId);
    ProfileDTO createOrUpdateProfile(Long userId, ProfileRequest profileRequest);
    void updatePassword(Long userId, String oldPassword, String newPassword);
}
