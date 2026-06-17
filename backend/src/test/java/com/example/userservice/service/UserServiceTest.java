package com.example.userservice.service;

import com.example.userservice.dto.AddressRequest;
import com.example.userservice.dto.AddressResponse;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UserSummaryResponse;
import com.example.userservice.dto.mapper.UserDtoMapper;
import com.example.userservice.exception.ConflictException;
import com.example.userservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new UserDtoMapper());
    }

    @Test
    void findUserById_throwsWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(999L));
    }

    @Test
    void updateUser_throwsOnDuplicateEmail() {
        UserRequest request = new UserRequest();
        request.setEmail("viktor@test.com");
        request.setFirstName("Chuck");
        request.setLastName("Rick");

        assertThrows(ConflictException.class, () -> userService.updateUser(3L, request));
    }

    @Test
    void addFirstAddress_setsPrimaryAutomatically() {
        AddressRequest request = new AddressRequest();
        request.setStreet("1 Main St");
        request.setCity("Boston");
        request.setState("MA");
        request.setZipCode("02101");
        request.setCountry("USA");
        request.setPrimary(false);

        AddressResponse address = userService.addAddress(4L, request);

        assertTrue(address.isPrimary());
    }

    @Test
    void updateOnlyAddress_cannotUnsetPrimary() {
        UserResponse bob = userService.findUserById(2L);
        Long addressId = bob.getAddresses().get(0).getId();

        AddressRequest update = new AddressRequest();
        update.setStreet("789 Pine Road");
        update.setCity("Austin");
        update.setState("TX");
        update.setZipCode("73301");
        update.setCountry("USA");
        update.setPrimary(false);

        assertThrows(ConflictException.class,
                () -> userService.updateAddress(2L, addressId, update));
    }

    @Test
    void deletePrimaryAddress_promotesNextAddress() {
        UserResponse viktor = userService.findUserById(1L);
        Long primaryId = viktor.getAddresses().stream()
                .filter(AddressResponse::isPrimary)
                .findFirst()
                .orElseThrow()
                .getId();
        Long secondaryId = viktor.getAddresses().stream()
                .filter(a -> !a.isPrimary())
                .findFirst()
                .orElseThrow()
                .getId();

        userService.deleteAddress(1L, primaryId);

        UserResponse updated = userService.findUserById(1L);
        AddressResponse promoted = updated.getAddresses().stream()
                .filter(a -> a.getId().equals(secondaryId))
                .findFirst()
                .orElseThrow();

        assertTrue(promoted.isPrimary());
        assertEquals(9, updated.getAddresses().size());
    }

    @Test
    void findAllUsers_returnsUnmodifiableList() {
        List<UserSummaryResponse> users = userService.findAllUsers();
        assertThrows(UnsupportedOperationException.class, () -> users.add(new UserSummaryResponse()));
    }

    @Test
    void findUserById_returnsDtoWithoutMutatingDomain() {
        UserResponse user = userService.findUserById(1L);
        assertNotNull(user.getId());
        assertFalse(user.getAddresses().isEmpty());
    }
}
