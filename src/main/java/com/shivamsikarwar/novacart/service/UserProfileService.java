package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.UserProfileResponseDto;
import com.shivamsikarwar.novacart.dto.UserProfileUpdateRequestDto;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.entity.UserProfile;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.UserProfileRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(
            UserRepository userRepository,
            UserProfileRepository userProfileRepository) {

        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public UserProfileResponseDto getMyProfile(String username) {

        User user = findUser(username);

        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseGet(() -> createProfile(user));

        return toResponseDto(profile);
    }

    @Transactional
    public UserProfileResponseDto updateMyProfile(
            String username,
            UserProfileUpdateRequestDto request) {

        User user = findUser(username);

        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseGet(() -> createProfile(user));


        if (request.getFirstName() != null) {
            profile.setFirstName(normalize(request.getFirstName()));
        }

        if (request.getLastName() != null) {
            profile.setLastName(normalize(request.getLastName()));
        }

        if (request.getPhone() != null) {
            profile.setPhone(normalize(request.getPhone()));
        }

        if (request.getBio() != null) {
            profile.setBio(normalize(request.getBio()));
        }

        if (request.getProfileImage() != null) {
            profile.setProfileImage(normalize(request.getProfileImage()));
        }

        UserProfile savedProfile = userProfileRepository.save(profile);

        return toResponseDto(savedProfile);
    }

    private User findUser(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    private UserProfile createProfile(User user) {

        UserProfile profile = new UserProfile();
        profile.setUser(user);

        return userProfileRepository.save(profile);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty() ? "" : trimmed;
    }

    private UserProfileResponseDto toResponseDto(UserProfile profile) {

        User user = profile.getUser();

        UserProfileResponseDto response =
                new UserProfileResponseDto();

        response.setId(profile.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setPhone(profile.getPhone());
        response.setBio(profile.getBio());
        response.setProfileImage(profile.getProfileImage());

        return response;
    }
}