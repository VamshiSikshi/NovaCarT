package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();
    }

    private User createUser(
            String username,
            String email,
            Role role) {

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password123");
        user.setRole(role);

        return userRepository.save(user);
    }

    @Test
    void findByUsernameReturnsUser() {

        createUser(
                "shivam",
                "shivam@example.com",
                Role.USER
        );

        Optional<User> result =
                userRepository.findByUsername("shivam");

        assertThat(result)
                .isPresent();

        assertThat(result.get().getUsername())
                .isEqualTo("shivam");

        assertThat(result.get().getEmail())
                .isEqualTo("shivam@example.com");

        assertThat(result.get().getRole())
                .isEqualTo(Role.USER);
    }

    @Test
    void findByUsernameWhenUserDoesNotExistReturnsEmpty() {

        Optional<User> result =
                userRepository.findByUsername("unknown");

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findByEmailReturnsUser() {

        createUser(
                "shivam",
                "shivam@example.com",
                Role.USER
        );

        Optional<User> result =
                userRepository.findByEmail(
                        "shivam@example.com"
                );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getUsername())
                .isEqualTo("shivam");
    }

    @Test
    void findByEmailWhenUserDoesNotExistReturnsEmpty() {

        Optional<User> result =
                userRepository.findByEmail(
                        "unknown@example.com"
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void existsByUsernameReturnsTrueForExistingUser() {

        User user =
                createUser(
                        "shivam",
                        "shivam@example.com",
                        Role.USER
                );

        assertThat(
                userRepository.existsByUsername(
                        user.getUsername()
                )
        ).isTrue();
    }

    @Test
    void existsByUsernameReturnsFalseForMissingUser() {

        assertThat(
                userRepository.existsByUsername("unknown")
        ).isFalse();
    }

    @Test
    void existsByEmailReturnsTrueForExistingUser() {

        User user =
                createUser(
                        "shivam",
                        "shivam@example.com",
                        Role.USER
                );

        assertThat(
                userRepository.existsByEmail(
                        user.getEmail()
                )
        ).isTrue();
    }

    @Test
    void existsByEmailReturnsFalseForMissingUser() {

        assertThat(
                userRepository.existsByEmail(
                        "unknown@example.com"
                )
        ).isFalse();
    }

    @Test
    void findAllByOrderByIdDescReturnsUsersInDescendingIdOrder() {

        User first =
                createUser(
                        "first",
                        "first@example.com",
                        Role.USER
                );

        User second =
                createUser(
                        "second",
                        "second@example.com",
                        Role.USER
                );

        User third =
                createUser(
                        "third",
                        "third@example.com",
                        Role.USER
                );

        Page<User> page =
                userRepository.findAllByOrderByIdDesc(
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .hasSize(3);

        assertThat(page.getContent().get(0).getId())
                .isEqualTo(third.getId());

        assertThat(page.getContent().get(1).getId())
                .isEqualTo(second.getId());

        assertThat(page.getContent().get(2).getId())
                .isEqualTo(first.getId());
    }

    @Test
    void findAllByOrderByIdDescSupportsPagination() {

        createUser(
                "first",
                "first@example.com",
                Role.USER
        );

        createUser(
                "second",
                "second@example.com",
                Role.USER
        );

        createUser(
                "third",
                "third@example.com",
                Role.USER
        );

        Page<User> page =
                userRepository.findAllByOrderByIdDesc(
                        PageRequest.of(0, 2)
                );

        assertThat(page.getContent())
                .hasSize(2);

        assertThat(page.getTotalElements())
                .isEqualTo(3);

        assertThat(page.getTotalPages())
                .isEqualTo(2);
    }

    @Test
    void findByRoleOrderByIdDescReturnsUsersWithRequestedRole() {

        User user1 =
                createUser(
                        "user1",
                        "user1@example.com",
                        Role.USER
                );

        User admin =
                createUser(
                        "admin",
                        "admin@example.com",
                        Role.ADMIN
                );

        User user2 =
                createUser(
                        "user2",
                        "user2@example.com",
                        Role.USER
                );

        Page<User> page =
                userRepository.findByRoleOrderByIdDesc(
                        Role.USER,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .hasSize(2);

        assertThat(page.getContent())
                .allSatisfy(user ->
                        assertThat(user.getRole())
                                .isEqualTo(Role.USER)
                );

        assertThat(page.getContent().get(0).getId())
                .isEqualTo(user2.getId());

        assertThat(page.getContent().get(1).getId())
                .isEqualTo(user1.getId());

        assertThat(page.getContent())
                .noneMatch(user ->
                        user.getId().equals(admin.getId())
                );
    }

    @Test
    void findByRoleOrderByIdDescReturnsEmptyPageWhenRoleDoesNotExist() {

        createUser(
                "user1",
                "user1@example.com",
                Role.USER
        );

        Page<User> page =
                userRepository.findByRoleOrderByIdDesc(
                        Role.ADMIN,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .isEmpty();

        assertThat(page.getTotalElements())
                .isZero();
    }

    @Test
    void countByRoleReturnsCorrectCount() {

        createUser(
                "user1",
                "user1@example.com",
                Role.USER
        );

        createUser(
                "user2",
                "user2@example.com",
                Role.USER
        );

        createUser(
                "admin",
                "admin@example.com",
                Role.ADMIN
        );

        assertThat(
                userRepository.countByRole(Role.USER)
        ).isEqualTo(2);

        assertThat(
                userRepository.countByRole(Role.ADMIN)
        ).isEqualTo(1);
    }

    @Test
    void countByRoleReturnsZeroWhenRoleHasNoUsers() {

        createUser(
                "user1",
                "user1@example.com",
                Role.USER
        );

        assertThat(
                userRepository.countByRole(Role.ADMIN)
        ).isZero();
    }
}
