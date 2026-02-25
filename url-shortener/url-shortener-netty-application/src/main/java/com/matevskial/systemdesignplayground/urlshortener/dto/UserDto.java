package com.matevskial.systemdesignplayground.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class UserDto {

    @JsonAlias("email")
    private final String username;
    @JsonProperty("qty") // (2)
    private final int quantity;

    @JsonCreator // (3)
    public UserDto(String username, int quantity) {
        this.username = username;
        this.quantity = quantity;
    }

    public String getUsername() {
        return username;
    }

    public int getQuantity() {
        return quantity;
    }
}
