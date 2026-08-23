package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.AddressRequestDto;
import com.shivamsikarwar.novacart.dto.AddressResponseDto;
import com.shivamsikarwar.novacart.entity.Address;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.AuthenticatedUserNotFoundException;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.AddressRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    // =========================
    // CREATE ADDRESS
    // =========================

    @Transactional
    public AddressResponseDto createAddress(
            AddressRequestDto request) {

        User user = getAuthenticatedUser();

        Address address = new Address();

        address.setUser(user);
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setType(request.getType());

        Address savedAddress =
                addressRepository.save(address);

        return mapToResponse(savedAddress);
    }

    // =========================
    // GET MY ADDRESSES
    // =========================

    @Transactional
    public List<AddressResponseDto> getMyAddresses() {

        User user = getAuthenticatedUser();

        return addressRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // GET ADDRESS BY ID
    // =========================

    @Transactional
    public AddressResponseDto getAddressById(
            Long addressId) {

        User user = getAuthenticatedUser();

        Address address =
                findAddressForUser(
                        addressId,
                        user.getId()
                );

        return mapToResponse(address);
    }

    // =========================
    // UPDATE ADDRESS
    // =========================

    @Transactional
    public AddressResponseDto updateAddress(
            Long addressId,
            AddressRequestDto request) {

        User user = getAuthenticatedUser();

        Address address =
                findAddressForUser(
                        addressId,
                        user.getId()
                );

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setType(request.getType());

        Address updatedAddress =
                addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }

    // =========================
    // DELETE ADDRESS
    // =========================

    @Transactional
    public void deleteAddress(Long addressId) {

        User user = getAuthenticatedUser();

        Address address =
                findAddressForUser(
                        addressId,
                        user.getId()
                );

        addressRepository.delete(address);
    }

    // =========================
    // FIND USER'S ADDRESS
    // =========================

    private Address findAddressForUser(
            Long addressId,
            Long userId) {

        return addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id: "
                                        + addressId
                        )
                );
    }

    // =========================
    // GET AUTHENTICATED USER
    // =========================

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AuthenticatedUserNotFoundException(
                    "Authenticated user not found"
            );
        }

        String username =
                authentication.getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AuthenticatedUserNotFoundException(
                                "Authenticated user not found: "
                                        + username
                        )
                );
    }

    // =========================
    // ENTITY → RESPONSE DTO
    // =========================

    private AddressResponseDto mapToResponse(
            Address address) {

        AddressResponseDto response =
                new AddressResponseDto();

        response.setId(address.getId());
        response.setFullName(address.getFullName());
        response.setPhone(address.getPhone());
        response.setAddressLine(address.getAddressLine());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPostalCode(address.getPostalCode());
        response.setCountry(address.getCountry());
        response.setType(address.getType());

        return response;
    }
}