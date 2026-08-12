package com.shivamsikarwar.novacart.dto;
import com.shivamsikarwar.novacart.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private Role role;
}
