package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.entity.PasswordResetToken;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.InvalidPasswordException;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.PasswordResetTokenRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shivamsikarwar.novacart.exception.InvalidPasswordResetTokenException;
import com.shivamsikarwar.novacart.exception.EmailNotFoundException;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AuthService {

    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder=passwordEncoder;
        this.emailService=emailService;
    }

    @Transactional
    public void requestPasswordReset(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EmailNotFoundException(
                                "Email does not exist"
                        )
                );

        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush();

        String token = generateResetToken();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(RESET_TOKEN_EXPIRATION_MINUTES)
        );
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        String resetLink =
                "http://localhost:5173/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );
    }

    private String generateResetToken() {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InvalidPasswordResetTokenException(
                                        "Invalid or expired password reset token"
                                )
                        );

        if (resetToken.isUsed()) {
            throw new InvalidPasswordResetTokenException(
                    "Invalid or expired password reset token"
            );
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidPasswordResetTokenException(
                    "Invalid or expired password reset token"
            );
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional
    public void changePassword(
            String username,
            String currentPassword,
            String newPassword,
            String confirmPassword) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new InvalidPasswordException(
                    "New password and confirm password do not match");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidPasswordException(
                    "New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}