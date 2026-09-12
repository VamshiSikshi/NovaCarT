package com.shivamsikarwar.novacart.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String email,
            String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("NovaCart - Password Reset");
        message.setText(
                "Hello,\n\n" +
                        "We received a request to reset your NovaCart password.\n\n" +
                        "Click the link below to reset your password:\n" +
                        resetLink + "\n\n" +
                        "This link will expire in 15 minutes.\n\n" +
                        "If you did not request a password reset, you can safely ignore this email.\n\n" +
                        "Regards,\n" +
                        "NovaCart Team"
        );

        mailSender.send(message);
    }
}