package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Profile;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.payload.dto.ProfileDTO;
import com.springboot.boxo.payload.request.ProfileRequest;
import com.springboot.boxo.repository.ProfileRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.service.ProfileService;
import com.springboot.boxo.service.StorageService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;

    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository, ModelMapper modelMapper, StorageService storageService, PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ProfileDTO getProfileByUserId(Long userId) {
        var profile = profileRepository.findByUserId(userId);
        if (profile == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        return mapToDTO(profile);
    }

    @Override
    public ProfileDTO createOrUpdateProfile(Long userId, ProfileRequest profileRequest) {
        MultipartFile avatarFile = profileRequest.getAvatar();
        var profile = profileRepository.findByUserId(userId);

        mapToEntity(profileRequest, profile);

        if (profile != null) {
            deleteOldAvatar(profile);

            uploadAvatar(profile, avatarFile);
        } else {
            profile = new Profile();
            profile.setUser(userRepository.findById(userId).orElseThrow());

            uploadAvatar(profile, avatarFile);
        }

        profileRepository.save(profile);
        return mapToDTO(profile);
    }

    private void deleteOldAvatar(Profile profile) {
        if (profile.getAvatarKey() != null) {
            storageService.deleteFileFromS3(profile.getAvatarKey());
        }
    }

    private void uploadAvatar(Profile profile, MultipartFile avatarFile) {
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Upload new avatar and store the key
            String filename = generateAvatarFilename(profile.getUser().getId(), avatarFile);
            Map<String, String> uploadResult = storageService.uploadFileToS3(avatarFile, filename);

            if (!uploadResult.isEmpty()) {
                String url = uploadResult.get("url");
                String key = uploadResult.get("key");

                profile.setAvatar(url);
                profile.setAvatarKey(key);
            }
        }
    }

    private String generateAvatarFilename(Long userId, MultipartFile avatarFile) {
        return "avatar" + "-" + userId + "-" + avatarFile.getOriginalFilename();
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        var user = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private ProfileDTO mapToDTO(Profile profile) {
        return modelMapper.map(profile, ProfileDTO.class);
    }

    private void mapToEntity(ProfileRequest profileRequest, Profile profile) {
        ModelMapper localModelMapper = new ModelMapper();
        localModelMapper.typeMap(ProfileRequest.class, Profile.class)
                .addMappings(mapper -> mapper.skip(Profile::setAvatar))
                .addMappings(mapper -> mapper.skip(Profile::setAvatarKey))
                .addMappings(mapper -> mapper.skip(Profile::setUser));

        localModelMapper.map(profileRequest, profile);
    }
}
