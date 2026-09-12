package com.shivamsikarwar.novacart.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    @NotBlank
    @Size(min=3, max=50)
    @Pattern(
            regexp = "^[a-zA-Z0-9_.]+$",
            message = "Username can contain only letters, numbers, underscores, and dots"
    )
    private String username;

    @NotBlank
    @Email
    @Size(max=100)
    private String email;

    @NotBlank
    @Size(min=8, max=100)
    private String password;

}
