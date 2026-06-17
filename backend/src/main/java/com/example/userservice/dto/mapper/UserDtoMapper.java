package com.example.userservice.dto.mapper;

import com.example.userservice.dto.AddressRequest;
import com.example.userservice.model.User;
import com.example.userservice.dto.AddressResponse;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UserSummaryResponse;
import com.example.userservice.model.Address;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDtoMapper {

    public UserSummaryResponse toSummary(User user) {
        UserSummaryResponse response = new UserSummaryResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setAddressCount(user.getAddresses().size());
        return response;
    }

    public List<UserSummaryResponse> toSummaries(List<User> users) {
        return users.stream().map(this::toSummary).toList();
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setAddresses(toAddressResponses(user.getAddresses()));
        return response;
    }

    public AddressResponse toAddressResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setZipCode(address.getZipCode());
        response.setCountry(address.getCountry());
        response.setPrimary(address.isPrimary());
        return response;
    }

    public List<AddressResponse> toAddressResponses(List<Address> addresses) {
        return addresses.stream().map(this::toAddressResponse).toList();
    }

    public Address toAddress(AddressRequest request) {
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setPrimary(request.isPrimary());
        return address;
    }
}
