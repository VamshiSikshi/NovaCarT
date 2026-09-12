package com.shivamsikarwar.novacart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank
    @Size(max = 100)
    private String identifier;

    @NotBlank
    @Size(max = 100)
    private String password;
}
