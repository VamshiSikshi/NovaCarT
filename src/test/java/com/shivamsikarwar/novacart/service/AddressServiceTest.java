package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.AddressRequestDto;
import com.shivamsikarwar.novacart.dto.AddressResponseDto;
import com.shivamsikarwar.novacart.entity.Address;
import com.shivamsikarwar.novacart.entity.AddressType;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.AddressRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUpSecurityContext() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user1",
                        null,
                        List.of()
                )
        );
    }

    private User createUser() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        return user;
    }

    private AddressRequestDto createRequest() {

        AddressRequestDto request = new AddressRequestDto();

        request.setFullName("Shivam Sikarwar");
        request.setPhone("9876543210");
        request.setAddressLine("123 Main Street");
        request.setCity("Agra");
        request.setState("Uttar Pradesh");
        request.setPostalCode("282001");
        request.setCountry("India");
        request.setType(AddressType.HOME);

        return request;
    }

    @Test
    void createAddressCreatesAddressForAuthenticatedUser() {

        User user = createUser();
        AddressRequestDto request = createRequest();

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> {

                    Address address =
                            invocation.getArgument(0);

                    address.setId(10L);

                    return address;
                });

        AddressResponseDto response =
                addressService.createAddress(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(
                "Shivam Sikarwar",
                response.getFullName()
        );
        assertEquals(
                "Agra",
                response.getCity()
        );
        assertEquals(
                AddressType.HOME,
                response.getType()
        );

        verify(addressRepository)
                .save(any(Address.class));
    }

    @Test
    void getMyAddressesReturnsAuthenticatedUsersAddresses() {

        User user = createUser();

        Address address1 = new Address();
        address1.setId(10L);
        address1.setUser(user);
        address1.setFullName("Shivam Sikarwar");
        address1.setCity("Agra");
        address1.setType(AddressType.HOME);

        Address address2 = new Address();
        address2.setId(11L);
        address2.setUser(user);
        address2.setFullName("Shivam Sikarwar");
        address2.setCity("Gwalior");
        address2.setType(AddressType.WORK);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByUserId(1L))
                .thenReturn(List.of(address1, address2));

        List<AddressResponseDto> response =
                addressService.getMyAddresses();

        assertEquals(2, response.size());

        assertEquals(
                10L,
                response.get(0).getId()
        );

        assertEquals(
                11L,
                response.get(1).getId()
        );

        verify(addressRepository)
                .findByUserId(1L);
    }

    @Test
    void userCannotAccessAnotherUsersAddress() {

        User user = createUser();

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByIdAndUserId(99L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> addressService.getAddressById(99L)
        );

        verify(addressRepository)
                .findByIdAndUserId(99L, 1L);
    }

    @Test
    void updateAddressUpdatesOwnAddress() {

        User user = createUser();
        AddressRequestDto request = createRequest();

        Address address = new Address();
        address.setId(10L);
        address.setUser(user);
        address.setFullName("Old Name");
        address.setCity("Agra");
        address.setType(AddressType.HOME);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(address));

        when(addressRepository.save(address))
                .thenReturn(address);

        AddressResponseDto response =
                addressService.updateAddress(10L, request);

        assertNotNull(response);

        assertEquals(
                "Shivam Sikarwar",
                address.getFullName()
        );

        assertEquals(
                "Agra",
                address.getCity()
        );

        assertEquals(
                AddressType.HOME,
                address.getType()
        );

        verify(addressRepository)
                .save(address);
    }

    @Test
    void deleteAddressDeletesOwnAddress() {

        User user = createUser();

        Address address = new Address();
        address.setId(10L);
        address.setUser(user);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(address));

        addressService.deleteAddress(10L);

        verify(addressRepository)
                .delete(address);
    }

    @Test
    void userCannotDeleteAnotherUsersAddress() {

        User user = createUser();

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByIdAndUserId(99L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> addressService.deleteAddress(99L)
        );

        verify(addressRepository, never())
                .delete(any(Address.class));
    }

    @Test
    void updateAddressThrowsWhenAddressDoesNotExist() {

        User user = createUser();
        AddressRequestDto request = createRequest();

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(addressRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> addressService.updateAddress(999L, request)
        );

        verify(addressRepository, never())
                .save(any(Address.class));
    }
}