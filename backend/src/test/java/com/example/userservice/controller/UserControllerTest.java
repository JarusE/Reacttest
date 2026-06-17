package com.example.userservice.controller;

import com.example.userservice.dto.AddressRequest;
import com.example.userservice.dto.AddressResponse;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserSummaryResponse;
import com.example.userservice.exception.ConflictException;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_returnsSummaries() throws Exception {
        UserSummaryResponse summary = new UserSummaryResponse();
        summary.setId(1L);
        summary.setEmail("viktor@test.com");
        summary.setFirstName("Viktor");
        summary.setLastName("Viktorov");
        summary.setAddressCount(1);

        when(userService.findAllUsers()).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("viktor@test.com"))
                .andExpect(jsonPath("$[0].addressCount").value(1))
                .andExpect(jsonPath("$[0].addresses").doesNotExist());
    }

    @Test
    void getUser_returnsNotFound() throws Exception {
        when(userService.findUserById(99L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void updateUser_returnsConflict() throws Exception {
        UserRequest request = new UserRequest();
        request.setEmail("taken@example.com");
        request.setFirstName("Bob");
        request.setLastName("Black");

        when(userService.updateUser(eq(2L), any(UserRequest.class)))
                .thenThrow(new ConflictException("Email is already in use"));

        mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email is already in use"));
    }

    @Test
    void updateUser_returnsBadRequestOnValidation() throws Exception {
        UserRequest request = new UserRequest();
        request.setEmail("not-an-email");
        request.setFirstName("");
        request.setLastName("");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.firstName").exists());
    }

    @Test
    void addAddress_returnsCreated() throws Exception {
        AddressRequest request = new AddressRequest();
        request.setStreet("1 Main St");
        request.setCity("Boston");
        request.setState("MA");
        request.setZipCode("02101");
        request.setCountry("USA");
        request.setPrimary(true);

        AddressResponse address = new AddressResponse();
        address.setId(10L);
        address.setStreet("1 Main St");
        address.setCity("Boston");
        address.setState("MA");
        address.setZipCode("02101");
        address.setCountry("USA");
        address.setPrimary(true);

        when(userService.addAddress(eq(1L), any(AddressRequest.class))).thenReturn(address);

        mockMvc.perform(post("/api/users/1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.street").value("1 Main St"))
                .andExpect(jsonPath("$.primary").value(true));
    }

    @Test
    void deleteAddress_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1/addresses/5"))
                .andExpect(status().isNoContent());
    }
}
