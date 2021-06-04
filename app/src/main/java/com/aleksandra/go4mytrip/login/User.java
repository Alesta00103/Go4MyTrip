package com.aleksandra.go4mytrip.login;

public class User {
    private String UserId;
    private String Name;
    private String Email;
    private String ImageUser;

    public User() {

    }

    public User(String userId, String name, String email, String imageUser) {
        UserId = userId;
        Name = name;
        Email = email;
        ImageUser = imageUser;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setImageUser(String imageUser) {
        ImageUser = imageUser;
    }

    public String getUserId() {
        return UserId;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getImageUser() {
        return ImageUser;
    }
}
