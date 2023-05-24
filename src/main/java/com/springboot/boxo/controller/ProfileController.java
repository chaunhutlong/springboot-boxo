package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.ProfileDTO;
import com.springboot.boxo.payload.request.PasswordChangeRequest;
import com.springboot.boxo.payload.request.ProfileRequest;
import com.springboot.boxo.security.CustomUserDetails;
import com.springboot.boxo.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("${spring.data.rest.base-path}/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<ProfileDTO> getProfileByUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        var profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProfileDTO> createOrUpdateProfile(
            Authentication authentication,
            @Valid @ModelAttribute ProfileRequest profileRequest
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        var profile = profileService.createOrUpdateProfile(userId, profileRequest);
        return ResponseEntity.ok(profile);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(Authentication authentication, @RequestBody PasswordChangeRequest passwordChangeRequest) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        profileService.updatePassword(userId, passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword());
        return ResponseEntity.ok().build();
    }
}

