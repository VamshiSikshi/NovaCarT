package com.shivamsikarwar.novacart.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileUpdateRequestDto {

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Phone number must be a valid 10-digit Indian mobile number"
    )
    @Size(max = 15, message = "Phone must not exceed 15 characters")
    private String phone;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImage;
}