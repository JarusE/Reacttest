package com.example.userservice.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<Address> addresses = new ArrayList<>();

    public User() {
    }

    public User(Long id, String email, String firstName, String lastName, List<Address> addresses) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addresses = addresses != null ? addresses : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
