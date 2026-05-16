package com.example.fpappfront.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    private String token;

    private String username;
    @SerializedName("user_id")
    private int userId;

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public int getUserId(){
        return userId;
    }
}