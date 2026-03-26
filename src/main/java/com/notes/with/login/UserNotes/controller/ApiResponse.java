package com.notes.with.login.UserNotes.controller;

public class ApiResponse {
    public String message;
    public String name;
    public String email;
    public String token;

    public ApiResponse(String message, String name, String email, String token) {
        this.message = message;
        this.name = name;
        this.email = email;
        this.token = token;
    }
}