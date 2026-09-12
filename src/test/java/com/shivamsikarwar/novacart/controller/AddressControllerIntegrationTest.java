package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.entity.Address;
import com.shivamsikarwar.novacart.entity.AddressType;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.repository.AddressRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User secondUser;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);

        testUser = userRepository.save(testUser);

        secondUser = new User();
        secondUser.setUsername("seconduser");
        secondUser.setEmail("seconduser@example.com");
        secondUser.setPassword("password");
        secondUser.setRole(Role.USER);

        secondUser = userRepository.save(secondUser);
    }

    private Address createAddress(User user) {

        Address address = new Address();

        address.setUser(user);
        address.setFullName("Shivam Sikarwar");
        address.setPhone("9876543210");
        address.setAddressLine("123 Main Street");
        address.setCity("Agra");
        address.setState("Uttar Pradesh");
        address.setPostalCode("282001");
        address.setCountry("India");
        address.setType(AddressType.HOME);

        return addressRepository.save(address);
    }

    @Test
    void createAddressReturnsCreatedAddress() throws Exception {

        mockMvc.perform(
                        post("/api/addresses")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "fullName": "Shivam Sikarwar",
                                            "phone": "9876543210",
                                            "addressLine": "123 Main Street",
                                            "city": "Agra",
                                            "state": "Uttar Pradesh",
                                            "postalCode": "282001",
                                            "country": "India",
                                            "type": "HOME"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fullName", is("Shivam Sikarwar")))
                .andExpect(jsonPath("$.phone", is("9876543210")))
                .andExpect(jsonPath("$.addressLine", is("123 Main Street")))
                .andExpect(jsonPath("$.city", is("Agra")))
                .andExpect(jsonPath("$.state", is("Uttar Pradesh")))
                .andExpect(jsonPath("$.postalCode", is("282001")))
                .andExpect(jsonPath("$.country", is("India")))
                .andExpect(jsonPath("$.type", is("HOME")));
    }

    @Test
    void getMyAddressesReturnsOnlyAuthenticatedUsersAddresses() throws Exception {

        createAddress(testUser);
        createAddress(secondUser);

        mockMvc.perform(
                        get("/api/addresses")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName", is("Shivam Sikarwar")))
                .andExpect(jsonPath("$[0].city", is("Agra")));
    }

    @Test
    void getAddressByIdReturnsUsersAddress() throws Exception {

        Address address = createAddress(testUser);

        mockMvc.perform(
                        get("/api/addresses/{addressId}", address.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(address.getId().intValue())))
                .andExpect(jsonPath("$.fullName", is("Shivam Sikarwar")))
                .andExpect(jsonPath("$.phone", is("9876543210")))
                .andExpect(jsonPath("$.type", is("HOME")));
    }

    @Test
    void getAddressByIdWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        get("/api/addresses/{addressId}", 999999L)
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotAccessAnotherUsersAddress() throws Exception {

        Address address = createAddress(secondUser);

        mockMvc.perform(
                        get("/api/addresses/{addressId}", address.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAddressReturnsUpdatedAddress() throws Exception {

        Address address = createAddress(testUser);

        mockMvc.perform(
                        put("/api/addresses/{addressId}", address.getId())
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "fullName": "Updated Name",
                                            "phone": "9123456789",
                                            "addressLine": "456 Updated Street",
                                            "city": "Gwalior",
                                            "state": "Madhya Pradesh",
                                            "postalCode": "474001",
                                            "country": "India",
                                            "type": "WORK"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(address.getId().intValue())))
                .andExpect(jsonPath("$.fullName", is("Updated Name")))
                .andExpect(jsonPath("$.phone", is("9123456789")))
                .andExpect(jsonPath("$.addressLine", is("456 Updated Street")))
                .andExpect(jsonPath("$.city", is("Gwalior")))
                .andExpect(jsonPath("$.state", is("Madhya Pradesh")))
                .andExpect(jsonPath("$.postalCode", is("474001")))
                .andExpect(jsonPath("$.type", is("WORK")));
    }

    @Test
    void updateAddressWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        put("/api/addresses/{addressId}", 999999L)
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "fullName": "Updated Name",
                                            "phone": "9123456789",
                                            "addressLine": "456 Updated Street",
                                            "city": "Gwalior",
                                            "state": "Madhya Pradesh",
                                            "postalCode": "474001",
                                            "country": "India",
                                            "type": "WORK"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotUpdateAnotherUsersAddress() throws Exception {

        Address address = createAddress(secondUser);

        mockMvc.perform(
                        put("/api/addresses/{addressId}", address.getId())
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "fullName": "Hacked Name",
                                            "phone": "9123456789",
                                            "addressLine": "Hacked Address",
                                            "city": "Gwalior",
                                            "state": "Madhya Pradesh",
                                            "postalCode": "474001",
                                            "country": "India",
                                            "type": "OTHER"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAddressReturnsNoContent() throws Exception {

        Address address = createAddress(testUser);

        mockMvc.perform(
                        delete("/api/addresses/{addressId}", address.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNoContent());

        assertFalse(
                addressRepository
                        .findById(address.getId())
                        .isPresent()
        );
    }

    @Test
    void deleteAddressWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        delete("/api/addresses/{addressId}", 999999L)
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotDeleteAnotherUsersAddress() throws Exception {

        Address address = createAddress(secondUser);

        mockMvc.perform(
                        delete("/api/addresses/{addressId}", address.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void createAddressWithBlankFullNameReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/addresses")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "fullName": "",
                                            "phone": "9876543210",
                                            "addressLine": "123 Main Street",
                                            "city": "Agra",
                                            "state": "Uttar Pradesh",
                                            "postalCode": "282001",
                                            "country": "India",
                                            "type": "HOME"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAddressWithInvalidPhoneReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/addresses")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "fullName": "Shivam Sikarwar",
                                            "phone": "12345",
                                            "addressLine": "123 Main Street",
                                            "city": "Agra",
                                            "state": "Uttar Pradesh",
                                            "postalCode": "282001",
                                            "country": "India",
                                            "type": "HOME"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAddressWithInvalidPostalCodeReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/addresses")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "fullName": "Shivam Sikarwar",
                                            "phone": "9876543210",
                                            "addressLine": "123 Main Street",
                                            "city": "Agra",
                                            "state": "Uttar Pradesh",
                                            "postalCode": "123",
                                            "country": "India",
                                            "type": "HOME"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAddressWithMissingTypeReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/addresses")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "fullName": "Shivam Sikarwar",
                                            "phone": "9876543210",
                                            "addressLine": "123 Main Street",
                                            "city": "Agra",
                                            "state": "Uttar Pradesh",
                                            "postalCode": "282001",
                                            "country": "India"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void unauthenticatedUserCannotAccessAddresses() throws Exception {

        mockMvc.perform(
                        get("/api/addresses")
                )
                .andExpect(status().isUnauthorized());
    }
}