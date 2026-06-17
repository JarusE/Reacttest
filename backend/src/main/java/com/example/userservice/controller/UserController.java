package com.example.userservice.controller;

import com.example.userservice.dto.AddressRequest;
import com.example.userservice.dto.AddressResponse;
import jakarta.validation.Valid;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;

import com.example.userservice.dto.UserSummaryResponse;
import com.example.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserSummaryResponse> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequest request) {
        return userService.updateUser(userId, request);
    }

    @GetMapping("/{userId}/addresses")
    public List<AddressResponse> getAddresses(@PathVariable Long userId) {
        return userService.findAddressesByUserId(userId);
    }

    @PostMapping("/{userId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse addAddress(@PathVariable Long userId, @Valid @RequestBody AddressRequest request) {
        return userService.addAddress(userId, request);
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    public AddressResponse updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        return userService.updateAddress(userId, addressId, request);
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        userService.deleteAddress(userId, addressId);
    }
}
