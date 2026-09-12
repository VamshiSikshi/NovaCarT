package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.UserRequestDto;
import com.shivamsikarwar.novacart.dto.UserResponseDto;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.AuthenticatedUserNotFoundException;
import com.shivamsikarwar.novacart.exception.ResourceAlreadyExistsException;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.shivamsikarwar.novacart.entity.UserProfile;
import com.shivamsikarwar.novacart.repository.UserProfileRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       UserProfileRepository userProfileRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userProfileRepository=userProfileRepository;
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException(
                    "Username already exists: " + request.getUsername()
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "Email already exists: " + request.getEmail()
            );
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);

        userProfileRepository.save(profile);

        return toResponseDto(savedUser);
    }

    public UserResponseDto toResponseDto(User user) {
        UserResponseDto response = new UserResponseDto();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;
    }

    public UserResponseDto getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AuthenticatedUserNotFoundException(
                                "Authenticated user not found: " + username
                        )
                );

        return toResponseDto(user);
    }
}
