package com.shivamsikarwar.novacart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileResponseDto {

    private Long id;
    private String username;
    private String email;

    private String firstName;
    private String lastName;
    private String phone;
    private String bio;
    private String profileImage;
}