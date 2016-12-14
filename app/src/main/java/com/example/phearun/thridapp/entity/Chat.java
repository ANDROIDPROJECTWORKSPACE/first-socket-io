package com.example.phearun.thridapp.entity;

/**
 * Created by Phearun on 12/14/2016.
 */

public class Chat {
    private String username;
    private String message;

    public Chat() {
    }

    public Chat(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
