package com.example.userservice.service;

import com.example.userservice.model.Address;
import com.example.userservice.model.User;
import com.example.userservice.dto.AddressRequest;
import com.example.userservice.dto.AddressResponse;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UserSummaryResponse;
import com.example.userservice.dto.mapper.UserDtoMapper;
import com.example.userservice.exception.ConflictException;
import com.example.userservice.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();
    private final AtomicLong userIdSequence = new AtomicLong(1);
    private final AtomicLong addressIdSequence = new AtomicLong(1);
    private final UserDtoMapper mapper;

    public UserService(UserDtoMapper mapper) {
        this.mapper = mapper;
        seedData();
    }

    private void seedData() {
        User viktor = new User(
                userIdSequence.getAndIncrement(),
                "viktor@test.com",
                "Viktor",
                "Viktorov",
                new ArrayList<>(List.of(
                        address("123 Maple Street", "Springfield", "IL", "62704", "USA", true),
                        address("45 Oak Avenue", "Chicago", "IL", "60601", "USA", false),
                        address("88 River Road", "Denver", "CO", "80202", "USA", false),
                        address("12 Lake View", "Seattle", "WA", "98101", "USA", false),
                        address("500 Hill Street", "Portland", "OR", "97201", "USA", false),
                        address("77 Sunset Blvd", "Los Angeles", "CA", "90028", "USA", false),
                        address("9 Harbor Lane", "Miami", "FL", "33101", "USA", false),
                        address("301 Park Avenue", "New York", "NY", "10022", "USA", false),
                        address("14 Beacon Hill", "Boston", "MA", "02108", "USA", false),
                        address("62 Market Street", "San Francisco", "CA", "94105", "USA", false)
                ))
        );

        User bob = new User(
                userIdSequence.getAndIncrement(),
                "bob@test.com",
                "Bob",
                "Black",
                new ArrayList<>(List.of(
                        address("789 Pine Road", "Austin", "TX", "73301", "USA", true)
                ))
        );

        User chuck = new User(
                userIdSequence.getAndIncrement(),
                "chuck@test.com",
                "Chuck",
                "Rick",
                new ArrayList<>(List.of(
                        address("21 Cedar Court", "Dallas", "TX", "75201", "USA", true),
                        address("33 Birch Way", "Houston", "TX", "77002", "USA", false),
                        address("8 Elm Drive", "San Antonio", "TX", "78205", "USA", false)
                ))
        );

        User emptyTest = new User(
                userIdSequence.getAndIncrement(),
                "empty@test.com",
                "Empty",
                "Test",
                new ArrayList<>()
        );

        users.add(viktor);
        users.add(bob);
        users.add(chuck);
        users.add(emptyTest);
    }

    private Address address(String street, String city, String state, String zipCode, String country, boolean primary) {
        return new Address(addressIdSequence.getAndIncrement(), street, city, state, zipCode, country, primary);
    }

    public List<UserSummaryResponse> findAllUsers() {
        return Collections.unmodifiableList(mapper.toSummaries(users));
    }

    public UserResponse findUserById(Long id) {
        return mapper.toUserResponse(findUserEntityById(id));
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUserEntityById(id);
        ensureEmailAvailable(request.getEmail(), id);
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return mapper.toUserResponse(user);
    }

    public List<AddressResponse> findAddressesByUserId(Long userId) {
        return Collections.unmodifiableList(
                mapper.toAddressResponses(new ArrayList<>(findUserEntityById(userId).getAddresses()))
        );
    }

    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = findUserEntityById(userId);
        Address address = mapper.toAddress(request);
        address.setId(addressIdSequence.getAndIncrement());

        if (user.getAddresses().isEmpty()) {
            address.setPrimary(true);
        } else if (address.isPrimary()) {
            clearPrimaryFlags(user);
        }

        user.getAddresses().add(address);
        return mapper.toAddressResponse(address);
    }

    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        User user = findUserEntityById(userId);
        Address address = findAddress(user, addressId);

        if (request.isPrimary()) {
            clearPrimaryFlags(user);
        } else if (address.isPrimary() && user.getAddresses().size() == 1) {
            throw new ConflictException("The only address must remain primary");
        }

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setPrimary(request.isPrimary());
        return mapper.toAddressResponse(address);
    }

    public void deleteAddress(Long userId, Long addressId) {
        User user = findUserEntityById(userId);
        Address address = findAddress(user, addressId);
        boolean wasPrimary = address.isPrimary();
        user.getAddresses().remove(address);

        if (wasPrimary && !user.getAddresses().isEmpty()) {
            user.getAddresses().get(0).setPrimary(true);
        }
    }

    private User findUserEntityById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private void ensureEmailAvailable(String email, Long currentUserId) {
        boolean taken = users.stream()
                .anyMatch(user -> !user.getId().equals(currentUserId)
                        && user.getEmail().equalsIgnoreCase(email));
        if (taken) {
            throw new ConflictException("Email is already in use: " + email);
        }
    }

    private void clearPrimaryFlags(User user) {
        user.getAddresses().forEach(a -> a.setPrimary(false));
    }

    private Address findAddress(User user, Long addressId) {
        return user.getAddresses().stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId + " for user: " + user.getId()));
    }
}
