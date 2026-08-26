package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.UserResponseDto;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserService adminUserService;

    private User admin;
    private User user;
    private Pageable pageable;

    @BeforeEach
    void setUp() {

        admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);

        user = new User();
        user.setId(2L);
        user.setUsername("user");
        user.setEmail("user@example.com");
        user.setRole(Role.USER);

        pageable = PageRequest.of(0, 10);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of()
                )
        );
    }

    @Test
    void getAllUsersReturnsMappedUsers() {

        Page<User> page =
                new PageImpl<>(
                        List.of(admin, user),
                        pageable,
                        2
                );

        UserResponseDto adminResponse =
                new UserResponseDto();

        adminResponse.setId(1L);
        adminResponse.setUsername("admin");
        adminResponse.setEmail("admin@example.com");
        adminResponse.setRole(Role.ADMIN);

        UserResponseDto userResponse =
                new UserResponseDto();

        userResponse.setId(2L);
        userResponse.setUsername("user");
        userResponse.setEmail("user@example.com");
        userResponse.setRole(Role.USER);

        when(userRepository.findAllByOrderByIdDesc(pageable))
                .thenReturn(page);

        when(userService.toResponseDto(admin))
                .thenReturn(adminResponse);

        when(userService.toResponseDto(user))
                .thenReturn(userResponse);

        Page<UserResponseDto> response =
                adminUserService.getAllUsers(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals(2, response.getContent().size());

        assertEquals(
                "admin",
                response.getContent().get(0).getUsername()
        );

        assertEquals(
                "user",
                response.getContent().get(1).getUsername()
        );

        verify(userRepository)
                .findAllByOrderByIdDesc(pageable);

        verify(userService)
                .toResponseDto(admin);

        verify(userService)
                .toResponseDto(user);
    }

    @Test
    void getUsersByRoleReturnsUsersWithRequestedRole() {

        Page<User> page =
                new PageImpl<>(
                        List.of(user),
                        pageable,
                        1
                );

        UserResponseDto responseDto =
                new UserResponseDto();

        responseDto.setId(2L);
        responseDto.setUsername("user");
        responseDto.setEmail("user@example.com");
        responseDto.setRole(Role.USER);

        when(userRepository.findByRoleOrderByIdDesc(
                Role.USER,
                pageable
        )).thenReturn(page);

        when(userService.toResponseDto(user))
                .thenReturn(responseDto);

        Page<UserResponseDto> response =
                adminUserService.getUsersByRole(
                        Role.USER,
                        pageable
                );

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(
                "user",
                response.getContent()
                        .get(0)
                        .getUsername()
        );

        assertEquals(
                Role.USER,
                response.getContent()
                        .get(0)
                        .getRole()
        );

        verify(userRepository)
                .findByRoleOrderByIdDesc(
                        Role.USER,
                        pageable
                );

        verify(userService)
                .toResponseDto(user);
    }

    @Test
    void getUserByIdReturnsUser() {

        UserResponseDto responseDto =
                new UserResponseDto();

        responseDto.setId(2L);
        responseDto.setUsername("user");
        responseDto.setEmail("user@example.com");
        responseDto.setRole(Role.USER);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(userService.toResponseDto(user))
                .thenReturn(responseDto);

        UserResponseDto response =
                adminUserService.getUserById(2L);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("user", response.getUsername());
        assertEquals(Role.USER, response.getRole());

        verify(userRepository)
                .findById(2L);

        verify(userService)
                .toResponseDto(user);
    }

    @Test
    void getUserByIdThrowsWhenUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminUserService.getUserById(999L)
        );

        verify(userRepository)
                .findById(999L);

        verify(userService, never())
                .toResponseDto(any(User.class));
    }

    @Test
    void updateUserRoleRejectsNullRole() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.updateUserRole(
                        2L,
                        null
                )
        );

        verify(userRepository, never())
                .findById(anyLong());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserRoleThrowsWhenTargetUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminUserService.updateUserRole(
                        999L,
                        Role.ADMIN
                )
        );

        verify(userRepository)
                .findById(999L);

        verify(userRepository, never())
                .findByUsername(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserRoleThrowsWhenAuthenticatedAdminDoesNotExist() {

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminUserService.updateUserRole(
                        2L,
                        Role.ADMIN
                )
        );

        verify(userRepository)
                .findById(2L);

        verify(userRepository)
                .findByUsername("admin");

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserRolePreventsAdminFromChangingOwnRole() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(admin));

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.updateUserRole(
                        1L,
                        Role.USER
                )
        );

        verify(userRepository)
                .findById(1L);

        verify(userRepository)
                .findByUsername("admin");

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserRoleSuccessfullyChangesRole() {

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        user.setRole(Role.ADMIN);

        when(userRepository.save(user))
                .thenReturn(user);

        UserResponseDto responseDto =
                new UserResponseDto();

        responseDto.setId(2L);
        responseDto.setUsername("user");
        responseDto.setEmail("user@example.com");
        responseDto.setRole(Role.ADMIN);

        when(userService.toResponseDto(user))
                .thenReturn(responseDto);

        UserResponseDto response =
                adminUserService.updateUserRole(
                        2L,
                        Role.ADMIN
                );

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(Role.ADMIN, response.getRole());
        assertEquals(Role.ADMIN, user.getRole());

        verify(userRepository)
                .findById(2L);

        verify(userRepository)
                .findByUsername("admin");

        verify(userRepository)
                .save(user);

        verify(userService)
                .toResponseDto(user);
    }
}