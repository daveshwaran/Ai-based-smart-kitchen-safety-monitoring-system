package com.smartkitchen.smart_kitchen_backend.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
}