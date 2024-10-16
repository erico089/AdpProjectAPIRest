package com.example.APIAuth.model;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("password")
    private String password;

    // Constructor vacío (necesario para la deserialización)
    public LoginRequest() {}

    public LoginRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

