package com.shivamsikarwar.novacart.dto;

import com.shivamsikarwar.novacart.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateRequestDto {

    @NotNull(message = "Role is required")
    private Role role;
}