package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.UserRequestDto;
import com.shivamsikarwar.novacart.dto.UserResponseDto;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.ResourceAlreadyExistsException;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDto request;
    private User user;

    @BeforeEach
    void setUp() {

        request = new UserRequestDto();

        request.setUsername("shivam");
        request.setEmail("shivam@example.com");
        request.setPassword("password123");

        user = new User();

        user.setId(1L);
        user.setUsername("shivam");
        user.setEmail("shivam@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);
    }

    @Test
    void createUserCreatesUserSuccessfully() {

        when(userRepository.existsByUsername("shivam"))
                .thenReturn(false);

        when(userRepository.existsByEmail("shivam@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto response =
                userService.createUser(request);

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals("shivam", response.getUsername());
        assertEquals(
                "shivam@example.com",
                response.getEmail()
        );
        assertEquals(Role.USER, response.getRole());

        verify(userRepository)
                .existsByUsername("shivam");

        verify(userRepository)
                .existsByEmail("shivam@example.com");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void createUserRejectsDuplicateUsername() {

        when(userRepository.existsByUsername("shivam"))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository)
                .existsByUsername("shivam");

        verify(userRepository, never())
                .existsByEmail(anyString());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void createUserRejectsDuplicateEmail() {

        when(userRepository.existsByUsername("shivam"))
                .thenReturn(false);

        when(userRepository.existsByEmail("shivam@example.com"))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository)
                .existsByUsername("shivam");

        verify(userRepository)
                .existsByEmail("shivam@example.com");

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void createUserEncodesPasswordAndAssignsUserRole() {

        when(userRepository.existsByUsername("shivam"))
                .thenReturn(false);

        when(userRepository.existsByEmail("shivam@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UserResponseDto response =
                userService.createUser(request);

        assertEquals("shivam", response.getUsername());
        assertEquals(
                "shivam@example.com",
                response.getEmail()
        );
        assertEquals(Role.USER, response.getRole());

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(argThat(savedUser ->
                        savedUser.getUsername()
                                .equals("shivam")
                                && savedUser.getEmail()
                                .equals("shivam@example.com")
                                && savedUser.getPassword()
                                .equals("encoded-password")
                                && savedUser.getRole() == Role.USER
                ));
    }

    @Test
    void toResponseDtoMapsUserCorrectly() {

        UserResponseDto response =
                userService.toResponseDto(user);

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals("shivam", response.getUsername());
        assertEquals(
                "shivam@example.com",
                response.getEmail()
        );
        assertEquals(Role.USER, response.getRole());
    }
}