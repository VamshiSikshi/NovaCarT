package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Address;
import com.shivamsikarwar.novacart.entity.AddressType;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AddressRepositoryIntegrationTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private User testUser;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();

        testUser = createUser(
                "testuser",
                "testuser@example.com"
        );
    }

    private User createUser(
            String username,
            String email) {

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password123");
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    private Address createAddress(
            User user,
            String fullName,
            String city,
            AddressType type) {

        Address address = new Address();

        address.setUser(user);
        address.setFullName(fullName);
        address.setPhone("9876543210");
        address.setAddressLine("123 Main Street");
        address.setCity(city);
        address.setState("Madhya Pradesh");
        address.setPostalCode("474001");
        address.setCountry("India");
        address.setType(type);

        return addressRepository.save(address);
    }

    @Test
    void findByUserIdReturnsAddressesBelongingToUser() {

        createAddress(
                testUser,
                "Shivam Sikarwar",
                "Gwalior",
                AddressType.HOME
        );

        createAddress(
                testUser,
                "Shivam Sikarwar",
                "Agra",
                AddressType.WORK
        );

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        createAddress(
                anotherUser,
                "Another User",
                "Delhi",
                AddressType.HOME
        );

        List<Address> addresses =
                addressRepository.findByUserId(
                        testUser.getId()
                );

        assertThat(addresses)
                .hasSize(2);

        assertThat(addresses)
                .allSatisfy(address ->
                        assertThat(address.getUser().getId())
                                .isEqualTo(testUser.getId())
                );

        assertThat(addresses)
                .extracting(Address::getCity)
                .containsExactlyInAnyOrder(
                        "Gwalior",
                        "Agra"
                );
    }

    @Test
    void findByUserIdReturnsEmptyListWhenUserHasNoAddresses() {

        List<Address> addresses =
                addressRepository.findByUserId(
                        testUser.getId()
                );

        assertThat(addresses)
                .isEmpty();
    }

    @Test
    void findByIdAndUserIdReturnsAddressForCorrectUser() {

        Address savedAddress =
                createAddress(
                        testUser,
                        "Shivam Sikarwar",
                        "Gwalior",
                        AddressType.HOME
                );

        Optional<Address> result =
                addressRepository.findByIdAndUserId(
                        savedAddress.getId(),
                        testUser.getId()
                );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(savedAddress.getId());

        assertThat(result.get().getFullName())
                .isEqualTo("Shivam Sikarwar");

        assertThat(result.get().getCity())
                .isEqualTo("Gwalior");

        assertThat(result.get().getType())
                .isEqualTo(AddressType.HOME);
    }

    @Test
    void findByIdAndUserIdReturnsEmptyWhenAddressDoesNotExist() {

        Optional<Address> result =
                addressRepository.findByIdAndUserId(
                        999999L,
                        testUser.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findByIdAndUserIdReturnsEmptyWhenAddressBelongsToAnotherUser() {

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        Address address =
                createAddress(
                        anotherUser,
                        "Another User",
                        "Delhi",
                        AddressType.HOME
                );

        Optional<Address> result =
                addressRepository.findByIdAndUserId(
                        address.getId(),
                        testUser.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findByUserIdDoesNotReturnAddressesOfAnotherUser() {

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        createAddress(
                anotherUser,
                "Another User",
                "Delhi",
                AddressType.HOME
        );

        List<Address> addresses =
                addressRepository.findByUserId(
                        testUser.getId()
                );

        assertThat(addresses)
                .isEmpty();
    }
}