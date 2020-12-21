package com.wordpress.herovickers.omup.models;

import java.util.Map;

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileUrl;
    private String userId;
    private Map<String, Object> wallet;
    private String country;
    private Map<String, String> password;
    public User(String email, String firstName,
                String lastName, String phoneNumber,
                String profileUrl, String userId,
                Map<String, Object> wallet, String country, Map<String, String> password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileUrl = profileUrl;
        this.userId = userId;
        this.wallet = wallet;
        this.country = country;
        this.password = password;
    }

    public User() {
    }

    public Map<String, String> getPassword() {
        return password;
    }

    public void setPassword(Map<String, String> password) {
        this.password = password;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, Object> getWallet() {
        return wallet;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWallet(Map<String, Object> wallet) {
        this.wallet = wallet;
    }
}
