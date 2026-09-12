package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.AddressRequestDto;
import com.shivamsikarwar.novacart.dto.AddressResponseDto;
import com.shivamsikarwar.novacart.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // =========================
    // CREATE ADDRESS
    // =========================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponseDto createAddress(
            @Valid @RequestBody AddressRequestDto request) {

        return addressService.createAddress(request);
    }

    // =========================
    // GET MY ADDRESSES
    // =========================

    @GetMapping
    public List<AddressResponseDto> getMyAddresses() {

        return addressService.getMyAddresses();
    }

    // =========================
    // GET ADDRESS BY ID
    // =========================

    @GetMapping("/{addressId}")
    public AddressResponseDto getAddressById(
            @PathVariable Long addressId) {

        return addressService.getAddressById(addressId);
    }

    // =========================
    // UPDATE ADDRESS
    // =========================

    @PutMapping("/{addressId}")
    public AddressResponseDto updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto request) {

        return addressService.updateAddress(
                addressId,
                request
        );
    }

    // =========================
    // DELETE ADDRESS
    // =========================

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(
            @PathVariable Long addressId) {

        addressService.deleteAddress(addressId);
    }
}