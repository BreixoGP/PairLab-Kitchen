package com.example.fpappfront.data.model;

public class UpdateUserRequest {

    public String username;
    public String email;
    public String old_password;
    public String new_password;

    public UpdateUserRequest(String username, String email, String old_password, String new_password) {
        this.username = username;
        this.email = email;
        this.old_password = old_password;
        this.new_password = new_password;
    }

    public UpdateUserRequest() {

    }
}