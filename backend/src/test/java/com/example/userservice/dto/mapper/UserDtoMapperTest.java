package com.example.userservice.dto.mapper;

import com.example.userservice.model.Address;
import com.example.userservice.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoMapperTest {

    private final UserDtoMapper mapper = new UserDtoMapper();

    @Test
    void toSummary_mapsAddressCount() {
        User user = new User(1L, "a@b.com", "A", "B", List.of(new Address(), new Address()));

        var summary = mapper.toSummary(user);

        assertEquals(2, summary.getAddressCount());
        assertEquals("a@b.com", summary.getEmail());
    }

    @Test
    void toUserResponse_mapsNestedAddresses() {
        Address address = new Address(5L, "Main", "City", "ST", "12345", "USA", true);
        User user = new User(1L, "a@b.com", "A", "B", List.of(address));

        var response = mapper.toUserResponse(user);

        assertEquals(1, response.getAddresses().size());
        assertEquals(5L, response.getAddresses().get(0).getId());
        assertTrue(response.getAddresses().get(0).isPrimary());
    }

    @Test
    void toAddress_mapsRequestFields() {
        var request = new com.example.userservice.dto.AddressRequest();
        request.setStreet("1 Main");
        request.setCity("Boston");
        request.setState("MA");
        request.setZipCode("02101");
        request.setCountry("USA");
        request.setPrimary(true);

        Address address = mapper.toAddress(request);

        assertEquals("1 Main", address.getStreet());
        assertTrue(address.isPrimary());
    }
}
