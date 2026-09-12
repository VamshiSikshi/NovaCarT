package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.UserProfileResponseDto;
import com.shivamsikarwar.novacart.dto.UserProfileUpdateRequestDto;
import com.shivamsikarwar.novacart.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(
            Authentication authentication) {

        UserProfileResponseDto response =
                userProfileService.getMyProfile(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponseDto> updateMyProfile(
            @Valid @RequestBody UserProfileUpdateRequestDto request,
            Authentication authentication) {

        UserProfileResponseDto response =
                userProfileService.updateMyProfile(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }
}