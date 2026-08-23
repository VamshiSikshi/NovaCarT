package com.shivamsikarwar.novacart.dto;

import com.shivamsikarwar.novacart.entity.AddressType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressResponseDto {

    private Long id;

    private String fullName;

    private String phone;

    private String addressLine;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private AddressType type;
}