package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.ChangePasswordRequestDto;
import com.shivamsikarwar.novacart.dto.LoginRequestDto;
import com.shivamsikarwar.novacart.dto.ResetPasswordRequestDto;
import com.shivamsikarwar.novacart.security.JwtService;
import com.shivamsikarwar.novacart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.shivamsikarwar.novacart.dto.ForgotPasswordRequestDto;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController (
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AuthService authService){

        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;
        this.authService=authService;
    }

    @PostMapping("/login")
    public String login(
            @Valid @RequestBody LoginRequestDto request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getIdentifier(),
                                request.getPassword()
                        )
                );

        return jwtService.generateToken(
                authentication.getName()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto request) {

        authService.requestPasswordReset(request.getEmail());

        return ResponseEntity.ok(
                "If an account exists for this email, " +
                        "a password reset link has been sent."
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto request) {

        authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                "Password reset successfully. Please login."
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto request,
            Authentication authentication) {

        authService.changePassword(
                authentication.getName(),
                request.currentPassword(),
                request.newPassword(),
                request.confirmPassword()
        );

        return ResponseEntity.ok("Password changed successfully.");
    }
}
